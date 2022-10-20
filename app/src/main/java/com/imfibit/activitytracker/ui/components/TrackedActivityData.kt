package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
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

    val recordVM = hiltViewModel<RecordViewModel>()


    if (dismissState.isDismissed(DismissDirection.EndToStart)){
        LaunchedEffect(record){
            val deleted = scaffoldState.snackbarHostState.showSnackbar("Record deleted", "Undo")

            if (deleted == SnackbarResult.Dismissed){
                dismissState.snapTo(DismissValue.Default)

                recordVM.deleteByActivity(record.id, record.activity_id)
            }else
                dismissState.reset()
        }
    }

    SwipeToDismiss(
        directions = setOf(DismissDirection.EndToStart),
        state = dismissState,
        background = {}
    ) {
        Record(activity = activity, record = record)
    }
}



@Composable
fun Record(
    activity: TrackedActivity,
    record: TrackedActivityRecord
){
    val openSessionDialog  = remember { mutableStateOf(false) }
    val openScoreDialog  = remember { mutableStateOf(false) }

    val recordVM = hiltViewModel<RecordViewModel>()

    if (openSessionDialog.value) DialogSession(
        display = openSessionDialog,
        record = (record as TrackedActivityTime),
        onUpdate = {from, to -> recordVM.updateSession(record.id, from, to)},
        onDelete = {recordVM.deleteRecord(record.id, TrackedActivity.Type.TIME)}
    )

    if (openScoreDialog.value) DialogScore(
        display = openScoreDialog,
        record = (record as TrackedActivityScore),
        onUpdate = {from, score -> recordVM.updateScore(record.id, from, score)},
        onDelete = {recordVM.deleteRecord(record.id, TrackedActivity.Type.SCORE)}
    )


    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .clickable(onClick = {
                when(record){
                    is TrackedActivityCompletion -> {}
                    is TrackedActivityScore -> openScoreDialog.value = true
                    is TrackedActivityTime -> openSessionDialog.value = true
                }
            }),
        shape = RoundedCornerShape(20.dp),
        color = Colors.SuperLight
    ) {
        val time = with(AnnotatedString.Builder()) {
            append(stringResource(id = R.string.time) + ": ")

            when(record){
                is TrackedActivityCompletion -> {
                    append(record.datetime_completed.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
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

        val metric = activity.type.getLabel(record.metric).value()


        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
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


            Box(contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp)) {
                MetricBlock(metric = metric)
            }

        }


    }

}

@Composable
private fun MetricBlock(metric: String){
    Box(
        modifier = Modifier
            .size(60.dp, 25.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50)),
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
