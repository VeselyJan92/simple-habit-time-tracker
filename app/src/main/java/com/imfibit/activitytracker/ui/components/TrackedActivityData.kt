package com.imfibit.activitytracker.ui.components

import android.util.Log
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.screens.timeline.TimelineVM
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackedActivityRecord(
    activity: TrackedActivity,
    record: TrackedActivityRecord,
    scaffoldState: ScaffoldState,
){

    val dismissState = rememberDismissState()

    if (dismissState.value != DismissValue.Default){
        LaunchedEffect(subject = record){

            val deleted = scaffoldState.snackbarHostState.showSnackbar("Record deleted", "Undo")

            if (deleted == SnackbarResult.Dismissed){
                dismissState.snapTo(DismissValue.Default)
                RepositoryTrackedActivity().deleteRecordById(activity.id, record.id)
            }else
                dismissState.reset()
        }

    }

    SwipeToDismiss(
        state = dismissState,
        background = {},
    ) {
        Record(activity = activity, record = record)
    }
}



@Composable
private fun Record(
    activity: TrackedActivity,
    record: TrackedActivityRecord
){
    val display  = remember { mutableStateOf(false) }
    EditDialogs(display, record)

    Surface(
        elevation = 2.dp, modifier = Modifier.height(60.dp)
            .clickable(onClick = {display.value = true})
            .padding(2.dp)
    ) {
        val time = with(AnnotatedString.Builder()) {
            append(stringResource(id = R.string.time) + ": ")

            when(record){
                is TrackedActivityCompletion -> {
                    append(record.date_completed.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                }
                is TrackedActivityScore -> {
                    append(record.datetime_completed.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                }
                is TrackedActivityTime ->{
                    append(record.datetime_start.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                    append(" - ")
                    append(record.datetime_end.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                }
            }

            toAnnotatedString()
        }

        val metric = activity.type.getComposeString(record.metric).invoke()


        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = activity.name,
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp
                    )
                )


                Text(
                    text = time,
                    modifier = Modifier.padding(start = 16.dp),
                    style = TextStyle.Default.copy(color = Color.Black.copy(alpha = 0.6f)),
                )
            }


            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight().padding(end = 8.dp)) {
                MetricBlock(metric = metric)
            }

        }


    }

}

@Composable
private fun MetricBlock(metric: String){
    Box(
        modifier = Modifier.size(60.dp, 25.dp).background(Colors.ChipGray, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ){

        Text(
            text = metric,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ))
    }
}


@Composable
private fun EditDialogs(display: MutableState<Boolean>, record: TrackedActivityRecord){
    when(record){
        is TrackedActivityCompletion -> { }
        is TrackedActivityScore -> {
            DialogScore(
                display,
                record.score,
                record.datetime_completed,
                record.id
            ) { datetime, score ->
                GlobalScope.launch {
                    AppDatabase.activityRep.scoreDAO.update(
                        record.copy(
                            datetime_completed = datetime,
                            score = score
                        )
                    )
                }
            }
        }
        is TrackedActivityTime -> {
            DialogSession(
                from = record.datetime_start,
                to = record.datetime_end,
                display = display,
                recordId = record.id,
                activityId= record.activity_id
            )
        }
    }
}

