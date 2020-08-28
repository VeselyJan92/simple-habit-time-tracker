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
import com.janvesely.activitytracker.core.leftShift
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class HistoryChart(context: Context, attr: AttributeSet?) : ScrollableChart(context, attr) {

    constructor(context: Context): this(context, null)

    lateinit var baseDate: LocalDate
    lateinit var baseLocation: RectF
    private lateinit var checkmarks: IntArray
    private lateinit var colors: IntArray
    private var columnHeight = 0f
    private var columnWidth = 0f
    private var dfMonth = DateTimeFormatter.ofPattern("MMM")
    private var dfYear =  DateTimeFormatter.ofPattern("yyyy")

    private var firstWeekday = 1

    private var headerOverflow = 0.0f
    private var headerTextOffset = 0f
    private var isBackgroundTransparent = false
    private var isEditable = false
    private var isNumerical = false
    private var nColumns = 0
    private var nDays = 0

    lateinit var pSquareBg: Paint
    lateinit var pSquareFg: Paint
    lateinit var pTextHeader: Paint

    private var previousMonth: String? = null
    private var previousYear: String? = null
    private var primaryColor = 0
    private var reverseTextColor = 0
    private var squareSpacing = 0f
    private var squareTextOffset = 0f
    private var target = 0
    private var textColor = 0
    private var todayPositionInColumn = 0




    init{
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

            positionToDate(motionEvent.getX(pointerId), motionEvent.getY(pointerId)) ?: return false

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

    fun initPaints() {
        pTextHeader = Paint()
        pTextHeader.textAlign = Align.LEFT
        pTextHeader.isAntiAlias = true
        pSquareBg = Paint()
        pSquareFg = Paint()
        pSquareFg.isAntiAlias = true
        pSquareFg.textAlign = Align.CENTER
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectF = baseLocation
        val width = columnWidth
        val spacing = squareSpacing

        rectF.set(0.0f, 0.0f, width - spacing,  width - spacing)

        baseLocation.offset(paddingLeft.toFloat(), paddingTop.toFloat())
        headerOverflow = 0.0f

        previousMonth = ""
        previousYear = ""

        pTextHeader.color = textColor
        updateDate()

        for (i in 0 until nColumns - 1) {
            drawColumn(canvas, baseLocation, baseDate, i)
            baseLocation.offset(columnWidth, -columnHeight)
            baseDate = baseDate.plusDays(7)
        }

        drawWeekdays(canvas, baseLocation)
    }

    /* access modifiers changed from: protected */
    public override fun onMeasure(i: Int, i2: Int) {
        setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2))
    }


    public override fun onSizeChanged(width: Int, height: Int, ow: Int, oh: Int) {

        val f = if (height < 8)
            ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION.toFloat()
        else
            height.toFloat()


        val vWidth = (f / 8.0f)
        setScrollerBucketSize(vWidth.toInt())
        squareSpacing = InterfaceUtils.dpToPixels(context, 1.0f)

        val min = min(f * 0.06f, 30f)

        pSquareFg.textSize = min
        pTextHeader.textSize = min
        squareTextOffset = pSquareFg.fontSpacing * 0.4f
        headerTextOffset = pTextHeader.fontSpacing * 0.3f

        val weekdayLabelWidth = weekdayLabelWidth + headerTextOffset
        val paddingRight = (paddingRight + paddingLeft).toFloat()

        columnWidth = vWidth
        columnHeight = 8.0f * vWidth


        nColumns = ((width.toFloat() - weekdayLabelWidth - paddingRight) / vWidth).toInt() + 1
        updateDate()
    }

    private fun drawWeekdays(canvas: Canvas, rectF: RectF) {
        val fontSpacing = pTextHeader.fontSpacing * 0.4f


        for (str in getWeekdays()) {
            rectF.offset(0.0f, columnWidth)
            canvas.drawText(
                str,
                rectF.left + headerTextOffset,
                rectF.centerY() + fontSpacing,
                pTextHeader
            )
        }
    }

    fun getWeekdays() = DateFormatSymbols.getInstance().shortWeekdays.sliceArray(1..7).leftShift( firstWeekday);

    private fun drawColumn(canvas: Canvas, rectangle: RectF, _date: LocalDate, position: Int) {
        var date = _date
        drawColumnHeader(canvas, rectangle, date)

        rectangle.offset(0.0f, columnWidth)
        for (row in 0..6) {
            if (position != nColumns - 2 || dataOffset != 0 || row <= todayPositionInColumn) {
                drawSquare(
                    canvas,
                    rectangle,
                    date,
                    dataOffset * 7 + nDays - (position + 1) * 7 + todayPositionInColumn - row
                )
            }
            date = date.plusDays(1L)
            rectangle.offset(0.0f, columnWidth)
        }
    }

    private fun drawColumnHeader(canvas: Canvas, rectF: RectF, date: LocalDate) {
        var format = dfMonth.format(date)
        val format2 = dfYear.format(date)

        when {
            format != previousMonth -> {
                previousMonth = format
            }
            format2 != previousYear -> {
                previousYear = format2
                format = format2
            }
            else -> {
                format = ""
            }
        }


        canvas.drawText(
            format ?: "",
            rectF.left + headerOverflow,
            rectF.bottom - headerTextOffset,
            pTextHeader
        )
        headerOverflow += pTextHeader.measureText(format) + columnWidth * 0.2f
        headerOverflow = Math.max(0.0f, headerOverflow - columnWidth)
    }

    private fun drawSquare(
        canvas: Canvas,
        rectangle: RectF,
        date: LocalDate,
        i: Int
    ) {
        pSquareBg.color = when {
            i >= checkmarks.size      -> colors[0]
            checkmarks[i] == 0        -> colors[0]
            checkmarks[i] < target    -> if (isNumerical) textColor else colors[1]
            else                      -> colors[2]
        }

        pSquareFg.color = reverseTextColor

        canvas.drawRect(rectangle, pSquareBg)
        canvas.drawText(
            date.dayOfMonth.toString(),
            rectangle.centerX(),
            rectangle.centerY() + squareTextOffset,
            pSquareFg
        )
    }

    private val weekdayLabelWidth: Float
        get() {
            var f = 0.0f
            for (measureText in DateFormatSymbols.getInstance().shortWeekdays) {
                f = Math.max(f, pSquareFg.measureText(measureText))
            }
            return f
        }

    private fun init() {
        isEditable = false
        checkmarks = IntArray(0)

        target = 2
        initColors()
        initPaints()
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


    private fun initRects() {
        baseLocation = RectF()
    }

    private fun positionToDate(x: Float, y: Float): LocalDate? {
        val weeks = (x / columnWidth).toInt()
        val days = (y / columnWidth).toInt()

        if (days == 0 || weeks == nColumns - 1)
            return null


        return if (baseDate.atStartOfDay() > LocalDate.now().atStartOfDay())
            null
        else
            baseDate.plusDays(weeks * 7L + (days - 1))

    }

    private fun updateDate() {
        nDays = (nColumns - 1) * 7
        todayPositionInColumn = (LocalDate.now().dayOfWeek.value + 7 - firstWeekday) % 7

        val x = -(dataOffset - 1) * 7 - nDays - todayPositionInColumn
        baseDate = LocalDate.now().plusDays(x.toLong())


    }
}