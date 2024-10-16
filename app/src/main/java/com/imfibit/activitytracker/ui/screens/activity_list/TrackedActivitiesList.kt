package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.navigate
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity.Type
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.Timer
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.ui.components.dialogs.ShowDialog
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.*
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


data class TrackedActivityRecentOverview(
    val activity: TrackedActivity,
    val challengeMetric: Long,
    val past: List<MetricWidgetData>,
    val actionButton: ActionButton = ActionButton.DEFAULT,
    val today: MetricAggregation
){
    enum class ActionButton{
        DEFAULT, CHECKED, IN_SESSION
    }

    override fun equals(other: Any?) = false

    override fun hashCode() = Random.nextInt()
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrackedActivity(
    nav: NavHostController,
    item: TrackedActivityRecentOverview,
    modifier: Modifier = Modifier,
    onNavigate: (activity: TrackedActivity) -> Unit,
    isDragging: Boolean = false
) {
    val activity = item.activity

    val recordVM = hiltViewModel<RecordViewModel>()


    val color = when{
        isDragging -> Color.LightGray
        activity.isInSession() -> Colors.SuperLight
        else -> Color.White
    }


    Surface(
        modifier = modifier
            .clickable(interactionSource = remember { MutableInteractionSource() } , indication = null) {
                onNavigate(activity)
            },

        elevation = 2.dp,
        color = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                    .animateContentSize()

            ) {

                ActionButton(
                    actionButton = item.actionButton,
                    activity = item.activity,
                    onClick = {
                        recordVM.activityTriggered(activity)
                    },
                    onLongClick = {
                        recordVM.hapticsService.activityFeedback()

                        when(activity.type){
                            Type.TIME ->  nav.navigate("dialog_edit_record/{record}", bundleOf("record" to TrackedActivityTime(activity_id = activity.id, datetime_start = LocalDateTime.now(), datetime_end = LocalDateTime.now())))
                            Type.SCORE ->  nav.navigate("dialog_edit_record/{record}", bundleOf("record" to TrackedActivityScore(activity_id = activity.id, datetime_completed = LocalDateTime.now(), score = 1)))
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
                            Goal(activity.type.getLabel(activity.goal.value).value())
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


                    AnimatedVisibility(
                        visible = item.activity.isInSession(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        // Fade in/out the background and the foreground.

                        Box(
                            Modifier.animateEnterExit(
                                enter = slideInVertically(),
                                exit = slideOutVertically()
                            ),
                        ) {
                            if(item.activity.isInSession()){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (activity.timer != null) {
                                        Text(text = stringResource(id = R.string.activity_in_timer) + " " + activity.type.getLabel(activity.timer?.toLong() ?: 0L).value())
                                    }else{
                                        Text(text = stringResource(id = R.string.activity_in_session) + " " + item.activity.inSessionSince!!.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    }

                                    Timer(
                                        startTime = item.activity.inSessionSince,
                                        onClick = {  }
                                    )
                                }
                            }
                        }

                    }
                }
            }

            if (item.activity.challenge.isSet()){
                GoalProgressBar(
                    item.activity.challenge,
                    item.challengeMetric,
                    item.activity.type
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.ActionButton(
    modifier: Modifier = Modifier,
    actionButton: ActionButton,
    activity: TrackedActivity,
    onClick: (()->Unit),
    onLongClick: (()->Unit) = {},
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .align(Alignment.CenterVertically)
            .size(55.dp)
            .padding(end = 8.dp, start = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {

        val icon = when (activity.type) {
            Type.TIME -> if (actionButton == ActionButton.IN_SESSION) Icons.Filled.Stop else Icons.Filled.PlayArrow
            Type.SCORE -> Icons.Filled.Add
            Type.CHECKED -> if (actionButton == ActionButton.CHECKED) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked
        }

        val color = if (actionButton == ActionButton.IN_SESSION) Color.Red else Colors.AppAccent

        val glow = remember {
            mutableStateOf(34.dp)
        }

        if (actionButton == ActionButton.IN_SESSION) LaunchedEffect(activity.inSessionSince){
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
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = Icons.Outlined.Flag,
            contentDescription = null
        )

        Text(
            label,
            Modifier.padding(start = 4.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )
        )
    }

}



