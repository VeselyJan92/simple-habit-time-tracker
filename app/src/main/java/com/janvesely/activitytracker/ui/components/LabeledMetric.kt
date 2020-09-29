package com.janvesely.activitytracker.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
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
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.core.App
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.components.dialogs.DialogSession
import com.janvesely.activitytracker.ui.components.dialogs.DialogScore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime


val labelHeight = 13.dp
val metricHeight = 20.dp
val metricTextStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.W600)


@Composable
@Preview
fun test(){

   // LabeledMetricBlock("00:00", "WEEK", Colors.AppAccent, width = 80.dp)

   Row(Modifier.width(300.dp)) {
        LabeledMetricBlock("00:00", "WEEK", Colors.AppAccent, width = 0.dp, modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(50)))
        BaseMetricBlock("00:00", Colors.ChipGray, labelOffset = true, width = 0.dp, modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(50)))
    }

}


data class Editable(
    val activityId: Long = 0,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val recordId: Long = 0,
){

    fun isRecord() = recordId != 0L

}

data class BaseMetricData(
    val type: TrackedActivity.Type,
    val metric: Long,
    val color: Color,
    val editable: Editable? = null,
    val label: @Composable ()->String
){
    companion object{
        fun getEmpty(type: TrackedActivity.Type = TrackedActivity.Type.SESSION) = BaseMetricData(type, -1L, Colors.ChipGray){ "-" }
    }

    @Composable
    fun formatMetric() = type.format(metric)

    @Composable
    fun formatLabel() = label.invoke()
}

@Composable
fun BaseMetricBlock(
    metric: String,
    color: Color,
    modifier: Modifier = Modifier,
    labelOffset: Boolean = false,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    clickable: (() -> Unit)? = null
){
    val clickable = if(clickable != null)
        Modifier.clickable(onClick = {}, onLongClick = clickable)
    else
        Modifier

    val width = if (width == 0.dp)
        Modifier.weight(1f).height(metricHeight)
    else
        Modifier.size(width, metricHeight)

    Stack(
        modifier =  Modifier
            .padding(top = if (labelOffset) labelHeight else 0.dp)
            .then(width)
            .background(color, RoundedCornerShape(10.dp))
            .then(modifier)
            .then(clickable)
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
    clickable: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    metricStyle: TextStyle =  metricTextStyle
){

    val clickable = if(clickable != null)
        Modifier.clickable(onClick = {}, onLongClick = clickable)
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
        BaseMetricBlock(metric, color, modifier.fillMaxWidth(), width = 0.dp, metricStyle = metricStyle)
    }

}



@OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
fun MetricBlock(
    data: BaseMetricData,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle
){
    val requestEdit = remember { mutableStateOf(false) }

    val rep = RepositoryTrackedActivity()

    var clickable: (() -> Unit)? = null

    if (editable){
        require(data.editable != null)

        clickable = if (editable) fun() {
            val viber = App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            viber.vibrate(VibrationEffect.createOneShot(50L, 1))

            requestEdit.value = true
        }else null

        if (requestEdit.value ) when (data.type){

            TrackedActivity.Type.SESSION -> DialogSession(
                from =  data.editable.from,
                to =  data.editable.to,
                display = requestEdit,
                recordId = data.editable.recordId,
                activityId = data.editable.activityId
            )

            TrackedActivity.Type.SCORE  -> DialogScore(
                display = requestEdit,
                datetime = data.editable.from,
                score = data.metric
            )

            TrackedActivity.Type.COMPLETED -> {
                GlobalScope.launch {
                    if (data.editable.isRecord())
                        rep.completionDAO.deleteById(data.editable.recordId)
                    else
                        rep.commitCompletion(data.editable.activityId, data.editable.from.toLocalDate())
                }

                requestEdit.value = false
            }
        }
    }

    LabeledMetricBlock(
        metric = data.formatMetric(),
        label = data.formatLabel(),
        color = data.color,
        clickable = clickable,
        modifier = modifier,
        width = width,
        metricStyle = metricStyle
    )
}