package com.imfibit.activitytracker.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
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


@OptIn(ExperimentalFoundationApi::class)
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
        Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    else
        Modifier

    val width = if (width == 0.dp)
        Modifier
            .fillMaxWidth()
            .height(metricHeight)
    else
        Modifier.size(width, metricHeight)


    Box(
        modifier = Modifier
            .padding(top = if (labelOffset) labelHeight else 0.dp)
            .then(width)
            .background(color, RoundedCornerShape(10.dp))
            .then(modifier)
            .then(clickable),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = metric,
            modifier = Modifier.align(Alignment.Center),
            style = metricStyle
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
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
        Modifier.combinedClickable(onClick = {}, onLongClick = onLongClick, )
    else
        Modifier

    val width = if (width == 0.dp){
        Modifier.fillMaxWidth(1f)
    }else{
        Modifier.width(width)
    }

    Column(
        modifier = Modifier
            .then(width)
            .height(metricHeight + labelHeight)
            .then(clickable),

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


@OptIn(ExperimentalFoundationApi::class)
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

    val context = LocalContext.current

    var onEdit: (() -> Unit)? = null

    if (isEditable){
        val editable = data.editable
        require(editable != null)

        onEdit = onLongClick ?: fun() {
            val viber = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            viber.vibrate(VibrationEffect.createOneShot(50L, 1))

            requestEdit.value = true
        }

        val vm = hiltViewModel<RecordViewModel>()

        if (requestEdit.value ) when (editable.type){
            TrackedActivity.Type.TIME -> DialogSession(
                display = requestEdit,
                allowDelete = editable.isRecord(),
                from =  if (editable.isRecord()) editable.from else editable.from.plusHours(12),
                to =  if (editable.isRecord()) editable.to else editable.from.plusHours(12),
                onUpdate = { from, to ->
                    if (editable.isRecord()){
                        vm.updateSession(editable.recordId, from, to )
                    }else{
                        vm.insertSession(editable.activityId, from, to)
                    }
                },
                onDelete = { vm.deleteRecord(editable.recordId, TrackedActivity.Type.TIME)}
            )
            TrackedActivity.Type.SCORE  -> DialogScore(
                display = requestEdit,
                allowDelete = editable.isRecord(),
                datetime = editable.from,
                score = if (editable.isRecord()) editable.metric else 1,
                onUpdate = {time, score ->
                    if (editable.isRecord())
                        vm.updateScore(editable.recordId, time, score)
                    else
                        vm.addScore(editable.activityId, time, score)
                },
                onDelete = { vm.deleteRecord(editable.recordId, TrackedActivity.Type.TIME)}
            )
            TrackedActivity.Type.CHECKED -> {
                vm.toggleHabit(editable.activityId, editable.from)
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