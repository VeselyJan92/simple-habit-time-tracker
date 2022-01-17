package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity.Type
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITY
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.burnoutcrew.reorderable.*
import java.time.LocalDateTime


data class TrackedActivityWithMetric constructor(
    val activity: TrackedActivity,
    val past: List<MetricWidgetData>,
    val hasMetricToday: Boolean
) {
}


@Composable
fun TrackedActivity(
    item: TrackedActivityWithMetric,
    modifier: Modifier = Modifier,
    onNavigate: (activity: TrackedActivity) -> Unit
) {
    val context = LocalContext.current
    val activity = item.activity

    val openSessionDialog = remember { mutableStateOf(false) }
    val openScoreDialog = remember { mutableStateOf(false) }

    val recordVM = hiltViewModel<RecordViewModel>()

    DialogSession(
        allowDelete = false,
        display = openSessionDialog,
        from = LocalDateTime.now(),
        to = LocalDateTime.now(),
        onUpdate = {from, to -> recordVM.insertSession(activity.id, from, to)}
    )

    DialogScore(
        allowDelete = false,
        display = openScoreDialog,
        datetime = LocalDateTime.now(),
        score = 1,
        onUpdate = {time, score -> recordVM.addScore(activity.id, time, score )}
    )


    Surface(
        modifier = modifier
            .clickable {
                onNavigate(activity)
            }
            .padding(2.dp),

        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)

        ) {

            ActionButton(
                hasMetricToday = item.hasMetricToday,
                activity = item.activity,
                onClick = {
                    recordVM.activityTriggered(activity)
                },
                onLongClick = {
                    (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                        VibrationEffect.createOneShot(50L, 1)
                    )

                    when(activity.type){
                        Type.TIME -> openSessionDialog.value = true
                        Type.SCORE -> openScoreDialog.value = true
                        Type.CHECKED -> {}
                    }
                }
            )


            Column(Modifier.fillMaxWidth()) {
                Row {
                    Text(
                        activity.name,
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if (activity.goal.isSet()) {
                        Goal(activity.type.getComposeString(activity.goal.value).invoke())
                    }

                }


                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricBlock(item.past[4], width = 80.dp, metricStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                    MetricBlock(item.past[3] )
                    MetricBlock(item.past[2] )
                    MetricBlock(item.past[1] )
                    MetricBlock(item.past[0])
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.ActionButton(
    hasMetricToday: Boolean,
    activity: TrackedActivity,
    onClick: (()->Unit),
    onLongClick: (()->Unit) = {},
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .size(55.dp)
            .padding(end = 8.dp, start = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {

        val icon = when (activity.type) {
            Type.TIME -> if (activity.inSessionSince != null) Icons.Filled.Stop else Icons.Filled.PlayArrow
            Type.SCORE -> Icons.Filled.Add
            Type.CHECKED -> if (hasMetricToday) Icons.Filled.DoneAll else Icons.Filled.Check
        }

        val color = if (activity.inSessionSince != null ) Color.Red else Colors.AppAccent

        val glow = remember {
            mutableStateOf(34.dp)
        }

        if (activity.inSessionSince!= null) LaunchedEffect(activity.inSessionSince){
            while (currentCoroutineContext().isActive){
                glow.value = if (glow.value == 34.dp) 37.dp else 34.dp
                delay(1000)
            }
        }

        Box(
            Modifier
                .size(glow.value)
                .background(color, RoundedCornerShape(50)), contentAlignment = Alignment.Center  ){
            Icon(
                contentDescription = null,
                imageVector = icon,
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}


@Composable
fun Goal(label: String) {
    Row(
        Modifier
            .size(70.dp, 20.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
    ) {
        Icon(Icons.Filled.Flag, contentDescription = null,)

        Text(
            label,
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )
        )
    }

}



