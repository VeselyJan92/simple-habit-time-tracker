package com.janvesely.activitytracker.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope.Companion.weight
import androidx.compose.ui.unit.sp
import com.janvesely.activitytracker.core.App
import com.janvesely.activitytracker.core.ComposeString
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.dialogs.DialogSession
import com.janvesely.activitytracker.ui.components.dialogs.DialogScore
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


val labelHeight = 13.dp
val metricHeight = 20.dp
val metricTextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W600)

data class Editable(
    val type: TrackedActivity.Type,
    val metric: Long,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val activityId: Long = 0,
    val recordId: Long = 0,
){
    fun isRecord() = recordId != 0L
}


sealed class MetricWidgetData(
    open val metric: ComposeString,
    open val color: Color,
    open val editable: Editable?,
){
    data class Base(
        override val metric: ComposeString,
        override val color: Color,
        override val editable: Editable? = null,
    ): MetricWidgetData(metric, color, editable)

    data class Labeled(
        val label: ComposeString,
        override val metric: ComposeString,
        override val color: Color,
        override val editable: Editable? = null,
    ): MetricWidgetData(metric, color, editable){

        @Composable
        fun formatLabel() = label.invoke()
    }

    @Composable
    fun formatMetric() = metric.invoke()
}


@Composable
fun BaseMetricBlock(
    metric: String,
    color: Color,
    modifier: Modifier = Modifier,
    labelOffset: Boolean = false,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
){
    val clickable = if(onClick != null)
        Modifier.clickable(onClick = onClick, onLongClick = onLongClick)
    else
        Modifier

    val width = if (width == 0.dp)
        Modifier.weight(1f).height(metricHeight)
    else
        Modifier.size(width, metricHeight)


    Box(
        modifier =  Modifier
            .padding(top = if (labelOffset) labelHeight else 0.dp)
            .then(width)
            .background(color, RoundedCornerShape(10.dp))
            .then(modifier)
            .then(clickable),
        alignment = Alignment.Center
    ){
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = metric,
            style = metricStyle
        )
    }
}


@Composable
fun LabeledMetricBlock(
    metric: String,
    label: String,
    color: Color, width: Dp = 40.dp,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    metricStyle: TextStyle =  metricTextStyle,
    onClick: (() -> Unit)? = null
){

    val clickable = if(onLongClick != null)
        Modifier.clickable(onClick = {}, onLongClick = onLongClick)
    else
        Modifier

    val width = if (width == 0.dp){
        Modifier.weight(1f)
    }else{
        Modifier.width(width)
    }

    Column(
        modifier = Modifier.then(width).height(metricHeight + labelHeight).then(clickable),

        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = TextStyle(fontSize = 10.sp), modifier = Modifier.height(labelHeight))
        BaseMetricBlock(
            metric = metric,
            color = color,
            modifier = modifier.fillMaxWidth(),
            width = 0.dp,
            metricStyle = metricStyle,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }

}


@OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
fun MetricBlock(
    data: MetricWidgetData,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
){
    val requestEdit = remember { mutableStateOf(false) }


    var onEdit: (() -> Unit)? = null

    if (isEditable){
        val editable = data.editable
        require(editable != null)

        onEdit = onLongClick ?: fun() {
            val viber = App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            viber.vibrate(VibrationEffect.createOneShot(50L, 1))

            requestEdit.value = true
        }

        if (requestEdit.value ) when (editable.type){
            TrackedActivity.Type.SESSION -> DialogSession(
                from =  editable.from,
                to =  editable.to,
                display = requestEdit,
                recordId = editable.recordId,
                activityId = editable.activityId
            )
            TrackedActivity.Type.SCORE  -> DialogScore(
                display = requestEdit,
                datetime = editable.from,
                score = editable.metric,
                recordId = editable.recordId,
                activityId = editable.activityId
            )
            TrackedActivity.Type.COMPLETED -> {
                GlobalScope.launch {
                    AppDatabase.activityRep.completionDAO.toggle(editable.activityId, editable.from.toLocalDate())
                }
                requestEdit.value = false
            }
        }
    }

    when(data){
        is MetricWidgetData.Base -> BaseMetricBlock(
            metric = data.formatMetric(),
            color = data.color,
            onLongClick = onEdit,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick,
        )

        is MetricWidgetData.Labeled ->  LabeledMetricBlock(
            metric = data.formatMetric(),
            label = data.formatLabel(),
            color = data.color,
            onLongClick = onEdit,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick
        )
    }



}