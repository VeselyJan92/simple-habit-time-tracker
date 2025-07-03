package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity.Type
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.TimerBlock
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton
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
    val today: MetricAggregation,
) {
    enum class ActionButton {
        DEFAULT, CHECKED, IN_SESSION
    }

    override fun equals(other: Any?) = false

    override fun hashCode() = Random.nextInt()
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrackedActivity(
    modifier: Modifier = Modifier,
    item: TrackedActivityRecentOverview,
    onNavigate: (activity: TrackedActivity) -> Unit,
    isDragging: Boolean = false,
    onActionButtonClick: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
) {
    val activity = item.activity

    val color = when {
        isDragging -> Color.LightGray
        activity.isInSession() -> Colors.SuperLight
        else -> Color.White
    }

    Surface(
        modifier = modifier,
        shadowElevation = 2.dp,
        color = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigate(activity)
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                    .animateContentSize()

            ) {

                val haptic = LocalHapticFeedback.current

                ActionButton(
                    actionButton = item.actionButton,
                    activity = item.activity,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onActionButtonClick(item.activity)
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        val record = when (activity.type) {
                            Type.TIME -> TrackedActivityTime(
                                activity_id = activity.id,
                                datetime_start = LocalDateTime.now(),
                                datetime_end = LocalDateTime.now()
                            )

                            Type.SCORE -> TrackedActivityScore(
                                activity_id = activity.id,
                                datetime_completed = LocalDateTime.now(),
                                score = 1
                            )

                            Type.CHECKED -> null
                        }

                        record?.let { onAddRecord(it) }
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricBlock(
                            item.past[4],
                            width = 80.dp,
                            metricStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        MetricBlock(item.past[3])
                        MetricBlock(item.past[2])
                        MetricBlock(item.past[1])
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
                            if (item.activity.isInSession()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    if (activity.timer != null) {
                                        Text(
                                            text = stringResource(id = R.string.activity_in_timer) + " " + activity.type.getLabel(
                                                activity.timer?.toLong() ?: 0L
                                            ).value()
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(id = R.string.activity_in_session) + " " + item.activity.inSessionSince!!.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                            )
                                        )
                                    }

                                    TimerBlock(
                                        startTime = item.activity.inSessionSince,
                                        onClick = { }
                                    )
                                }
                            }
                        }

                    }
                }
            }

            if (item.activity.challenge.isSet()) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    GoalProgressBar(
                        item.activity.challenge,
                        item.challengeMetric,
                        item.activity.type
                    )
                }
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
    onClick: (() -> Unit),
    onLongClick: (() -> Unit) = {},
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

        if (actionButton == ActionButton.IN_SESSION) LaunchedEffect(activity.inSessionSince) {
            while (currentCoroutineContext().isActive) {
                glow.value = if (glow.value == 34.dp) 37.dp else 34.dp
                delay(1000)
            }
        }

        Box(
            Modifier
                .size(glow.value)
                .background(color, RoundedCornerShape(50)), contentAlignment = Alignment.Center
        ) {
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



