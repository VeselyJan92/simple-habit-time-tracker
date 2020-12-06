package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val metricStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.W600
)

@Composable
fun TrackedActivityRecord(item: TrackedActivityData) = when (item){
    is TrackedActivityCompletion -> Completion(item)
    is TrackedActivityScore -> Score(item)
    is TrackedActivitySession -> Session(item)
}

@Composable
fun Completion(item: TrackedActivityCompletion) = BaseRecord(item, {}) {
    val text = with(AnnotatedString.Builder()) {
        pushStyle(SpanStyle(fontWeight = FontWeight.W700))
        append(item.date_completed.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))

        pop()
        append(item.date_completed.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))

        toAnnotatedString()
    }

    Text(
        text = text,
        modifier = Modifier
    )

    Box(
        modifier = Modifier.size(40.dp, 20.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))){

        Text(
            text = "2:00",
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontWeight = FontWeight.W600,
                fontSize = 12.sp
            )
        )
    }
}



@Composable
fun Score(item: TrackedActivityScore){
    val display  = remember { mutableStateOf(false) }

    DialogScore(display, item.score, item.time_completed, item.id){datetime, score ->
        GlobalScope.launch {
            AppDatabase.activityRep.scoreDAO.update(item.copy(time_completed = datetime, score = score))
        }
    }

    BaseRecord(item = item, click = { display.value = true }) {
        val text = with(AnnotatedString.Builder()) {
            pushStyle(SpanStyle(fontWeight = FontWeight.W700))
            append(item.time_completed.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
            append("   ")
            pop()
            append(item.time_completed.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))

            toAnnotatedString()
        }

        Text(
            text = text,
            modifier = Modifier
        )

        BaseMetricBlock(
            metric = TrackedActivity.Type.SCORE.format(item.score),
            color = Colors.ChipGray,
            metricStyle = metricStyle
        )
    }

}

@Composable
fun Session(item: TrackedActivitySession){

    var display  = remember { mutableStateOf(false) }

    DialogSession(
        from = item.time_start,
        to = item.time_end,
        display = display,
        recordId = item.id,
        activityId= item.activity_id
    )

    BaseRecord(item, {display.value = true}) {
        val text = with(AnnotatedString.Builder()) {
            pushStyle(SpanStyle(fontWeight = FontWeight.W700))
            append(item.time_start.format(DateTimeFormatter.ofPattern("dd. MM")))
            append("   ")
            pop()
            append(item.time_start.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
            append(" - ")
            append(item.time_end.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))

            toAnnotatedString()
        }

        Text(
            text = text,
            modifier = Modifier
        )

        BaseMetricBlock(
            TrackedActivity.Type.SESSION.format(item.getTimeInSeconds()),
            Colors.ChipGray,
            metricStyle = metricStyle
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BaseRecord(item: TrackedActivityData, click: ()->Unit, body: @Composable RowScope.()->Unit){
    Surface(
        elevation = 2.dp, modifier = Modifier.padding(bottom = 8.dp).height(40.dp).clickable(
            onClick = click
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding( 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = body
        )
    }





}
