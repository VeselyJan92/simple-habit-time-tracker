package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.entities.*
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


class MetricWidgetData(
    val value: ContextString,
    val color: Color,
    val label: ContextString? = null,
)


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
            .height(metricHeight)
    else
        Modifier.size(width, metricHeight)


    Box(
        modifier = Modifier
            .padding(top = if (labelOffset) labelHeight else 0.dp)
            .then(width)
            .background(color, RoundedCornerShape(8.dp))
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
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
){

    if(data.label == null) {
        BaseMetricBlock(
            metric = data.value.value(),
            color = data.color,
            onLongClick = onLongClick,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick,
        )
    }else{
        LabeledMetricBlock(
            metric = data.value.value(),
            label = data.label.value(),
            color = data.color,
            onLongClick = onLongClick,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick
        )
    }

}




