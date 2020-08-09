package com.janvesely.activitytracker.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class HistoryChart : ScrollableChart {
    private var baseDate: Calendar? = null
    private var baseLocation: RectF? = null
    private lateinit var checkmarks: IntArray
    private lateinit var colors: IntArray
    private var columnHeight = 0f
    private var columnWidth = 0f
    private var dfMonth: SimpleDateFormat? = null
    private var dfYear: SimpleDateFormat? = null
    private var firstWeekday = 1
    private var headerOverflow = 0.0f
    private var headerTextOffset = 0f
    private var isBackgroundTransparent = false
    private var isEditable = false
    private var isNumerical = false
    private var nColumns = 0
    private var nDays = 0
    private var pSquareBg: Paint? = null
    private var pSquareFg: Paint? = null
    private var pTextHeader: Paint? = null
    private var previousMonth: String? = null
    private var previousYear: String? = null
    private var primaryColor = 0
    private var reverseTextColor = 0
    private var squareSpacing = 0f
    private var squareTextOffset = 0f
    private var target = 0
    private var textColor = 0
    private var todayPositionInColumn = 0


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?
    ) : super(context, attributeSet) {
        init()
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        onSingleTapUp(motionEvent)
    }

    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        var i = 0
        if (!isEditable) {
            return false
        }
        performHapticFeedback(3)
        return try {
            val pointerId = motionEvent.getPointerId(0)
            positionToTimestamp(motionEvent.getX(pointerId), motionEvent.getY(pointerId))
                ?: return false
            val daysUntil: Int = 1
            val iArr = checkmarks
            if (daysUntil < iArr.size) {
                val z = iArr[daysUntil] == 2
                val iArr2 = checkmarks
                if (!z) {
                    i = 2
                }
                iArr2[daysUntil] = i
            }
            postInvalidate()
            true
        } catch (unused: RuntimeException) {
            false
        }
    }

    fun populateWithRandomData() {
        val random = Random()
        checkmarks = IntArray(100)
        for (i in 0..99) {
            if (random.nextFloat().toDouble() < 0.3) {
                checkmarks[i] = 2
            }
        }
        for (i2 in 0..92) {
            var i3 = 0
            for (i4 in 0..6) {
                if (checkmarks[i2 + i4] != 0) {
                    i3++
                }
            }
            if (i3 >= 3) {
                val iArr = checkmarks
                iArr[i2] = Math.max(iArr[i2], 1)
            }
        }
    }

    fun setCheckmarks(iArr: IntArray) {
        checkmarks = iArr
        postInvalidate()
    }

    fun setColor(i: Int) {
        primaryColor = i
        initColors()
        postInvalidate()
    }



    fun setNumerical(z: Boolean) {
        isNumerical = z
    }

    fun setIsBackgroundTransparent(z: Boolean) {
        isBackgroundTransparent = z
        initColors()
    }

    fun setIsEditable(z: Boolean) {
        isEditable = z
    }

    fun setTarget(i: Int) {
        target = i
        postInvalidate()
    }

    fun setFirstWeekday(i: Int) {
        firstWeekday = i
        postInvalidate()
    }

    /* access modifiers changed from: protected */
    fun initPaints() {
        pTextHeader = Paint()
        pTextHeader!!.textAlign = Align.LEFT
        pTextHeader!!.isAntiAlias = true
        pSquareBg = Paint()
        pSquareFg = Paint()
        pSquareFg!!.isAntiAlias = true
        pSquareFg!!.textAlign = Align.CENTER
    }

    /* access modifiers changed from: protected */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectF = baseLocation
        val f = columnWidth
        val f2 = squareSpacing
        rectF!![0.0f, 0.0f, f - f2] = f - f2
        baseLocation!!.offset(paddingLeft.toFloat(), paddingTop.toFloat())
        headerOverflow = 0.0f
        val str = ""
        previousMonth = str
        previousYear = str
        pTextHeader!!.color = textColor
        updateDate()
        val gregorianCalendar =
            baseDate!!.clone() as GregorianCalendar
        for (i in 0 until nColumns - 1) {
            drawColumn(canvas, baseLocation, gregorianCalendar, i)
            baseLocation!!.offset(columnWidth, -columnHeight)
        }
        drawAxis(canvas, baseLocation)
    }

    /* access modifiers changed from: protected */
    public override fun onMeasure(i: Int, i2: Int) {
        setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2))
    }

    /* access modifiers changed from: protected */
    public override fun onSizeChanged(i: Int, i2: Int, i3: Int, i4: Int) {
        var i2 = i2
        if (i2 < 8) {
            i2 = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION
        }
        val f = i2.toFloat()
        val f2 = f / 8.0f
        setScrollerBucketSize(f2.toInt())
        squareSpacing = InterfaceUtils.dpToPixels(context, 1.0f)

        val min = min(f * 0.06f, 30f)

        pSquareFg!!.textSize = min
        pTextHeader!!.textSize = min
        squareTextOffset = pSquareFg!!.fontSpacing * 0.4f
        headerTextOffset = pTextHeader!!.fontSpacing * 0.3f
        val weekdayLabelWidth = weekdayLabelWidth + headerTextOffset
        val paddingRight = (paddingRight + paddingLeft).toFloat()
        columnWidth = f2
        columnHeight = 8.0f * f2
        nColumns = ((i.toFloat() - weekdayLabelWidth - paddingRight) / f2).toInt() + 1
        updateDate()
    }

    private fun drawAxis(canvas: Canvas, rectF: RectF?) {
        val fontSpacing = pTextHeader!!.fontSpacing * 0.4f
        for (str in DateUtils.getShortWeekdayNames(firstWeekday)) {
            rectF!!.offset(0.0f, columnWidth)
            canvas.drawText(
                str,
                rectF.left + headerTextOffset,
                rectF.centerY() + fontSpacing,
                pTextHeader!!
            )
        }
    }

    private fun drawColumn(
        canvas: Canvas,
        rectF: RectF?,
        gregorianCalendar: GregorianCalendar,
        i: Int
    ) {
        drawColumnHeader(canvas, rectF, gregorianCalendar)
        rectF!!.offset(0.0f, columnWidth)
        for (i2 in 0..6) {
            if (i != nColumns - 2 || dataOffset != 0 || i2 <= todayPositionInColumn) {
                drawSquare(
                    canvas,
                    rectF,
                    gregorianCalendar,
                    dataOffset * 7 + nDays - (i + 1) * 7 + todayPositionInColumn - i2
                )
            }
            gregorianCalendar.add(5, 1)
            rectF.offset(0.0f, columnWidth)
        }
    }

    private fun drawColumnHeader(
        canvas: Canvas,
        rectF: RectF?,
        gregorianCalendar: GregorianCalendar
    ) {
        var format = dfMonth!!.format(gregorianCalendar.time)
        val format2 = dfYear!!.format(gregorianCalendar.time)
        if (format != previousMonth) {
            previousMonth = format
        } else if (format2 != previousYear) {
            previousYear = format2
            format = format2
        } else {
            format = null
        }

        canvas.drawText(
            format,
            rectF!!.left + headerOverflow,
            rectF.bottom - headerTextOffset,
            pTextHeader!!
        )
        headerOverflow += pTextHeader!!.measureText(format) + columnWidth * 0.2f
        headerOverflow = Math.max(0.0f, headerOverflow - columnWidth)
    }

    private fun drawSquare(
        canvas: Canvas,
        rectF: RectF?,
        gregorianCalendar: GregorianCalendar,
        i: Int
    ) {
        val iArr = checkmarks
        if (i >= iArr.size) {
            pSquareBg!!.color = colors[0]
        } else {
            val i2 = iArr[i]
            when {
                i2 == 0 -> {
                    pSquareBg!!.color = colors[0]
                }
                i2 < target -> {
                    pSquareBg!!.color = if (isNumerical) textColor else colors[1]
                }
                else -> {
                    pSquareBg!!.color = colors[2]
                }
            }
        }
        pSquareFg!!.color = reverseTextColor
        canvas.drawRect(rectF!!, pSquareBg!!)
        canvas.drawText(
            Integer.toString(gregorianCalendar[5]),
            rectF.centerX(),
            rectF.centerY() + squareTextOffset,
            pSquareFg!!
        )
    }

    private val weekdayLabelWidth: Float
        get() {
            var f = 0.0f
            for (measureText in DateUtils.getShortWeekdayNames(firstWeekday)) {
                f = Math.max(f, pSquareFg!!.measureText(measureText))
            }
            return f
        }

    private fun init() {
        isEditable = false
        checkmarks = IntArray(0)

        target = 2
        initColors()
        initPaints()
        initDateFormats()
        initRects()
        populateWithRandomData()
    }

    private fun initColors() {
        if (isBackgroundTransparent) {
            primaryColor = ColorUtils.setMinValue(primaryColor, 0.75f)
        }
        val red = Color.red(primaryColor)
        val green = Color.green(primaryColor)
        val blue = Color.blue(primaryColor)
        if (isBackgroundTransparent) {
            colors = IntArray(3)
            colors[0] = Color.argb(
                16,
                KotlinVersion.MAX_COMPONENT_VALUE,
                KotlinVersion.MAX_COMPONENT_VALUE,
                KotlinVersion.MAX_COMPONENT_VALUE
            )
            colors[1] = Color.argb(128, red, green, blue)
            colors[2] = primaryColor
            textColor = -1
            reverseTextColor = -1
            return
        }
        colors = IntArray(3)
        colors[0] = Color.BLUE
        colors[1] = Color.argb(127, red, green, blue)
        colors[2] = primaryColor
        textColor = Color.BLACK
        reverseTextColor = Color.GREEN
    }

    private fun initDateFormats() {
        val str = "yyyy"
        val str2 = "MMM"
        if (isInEditMode) {
            dfMonth = SimpleDateFormat(str2, Locale.getDefault())
            dfYear = SimpleDateFormat(str, Locale.getDefault())
            return
        }
        dfMonth = SimpleDateFormat("dd")
        dfYear = SimpleDateFormat("yyyy")
    }

    private fun initRects() {
        baseLocation = RectF()
    }

    private fun positionToTimestamp(f: Float, f2: Float): Timestamp? {
        val f3 = columnWidth
        val i = (f / f3).toInt()
        val i2 = (f2 / f3).toInt()
        if (i2 == 0 || i == nColumns - 1) {
            return null
        }
        val i3 = i * 7 + (i2 - 1)
        val calendar = baseDate!!.clone() as Calendar
        calendar.add(6, i3)
        return if (DateUtils.getStartOfDay(calendar.timeInMillis) > DateUtils.getStartOfToday()) {
            null
        } else Timestamp(calendar.timeInMillis)
    }

    private fun updateDate() {
        baseDate = DateUtils.getStartOfTodayCalendar()
        baseDate!!.add(6, -(dataOffset - 1) * 7)
        nDays = (nColumns - 1) * 7
        todayPositionInColumn =
            (DateUtils.getStartOfTodayCalendar().get(7) + 7 - firstWeekday) % 7
        baseDate!!.add(6, -nDays)
        baseDate!!.add(6, -todayPositionInColumn)
    }
}