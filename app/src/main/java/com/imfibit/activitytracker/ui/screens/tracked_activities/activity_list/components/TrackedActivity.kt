package com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity.Type
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.core.enums.MetricStatus
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.TimerBlock
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivityRecentOverview.ActionButton
import com.imfibit.activitytracker.ui.components.GoalProgressBar
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
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

    val backgroundColor = when {
        isDragging -> AppTheme.colors.lightBackground.copy(alpha = 0.9f)
        activity.isInSession() -> AppTheme.colors.onErrorContainer.copy(alpha = 0.8f) // Very soft pastel red/orange
        else -> AppTheme.colors.surfaceContainerLow.copy(alpha = 0.75f) // Translucent/frosted effect
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 0.dp, // Flattened out
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp) // tighter radius
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigate(activity)
                }
                .padding(14.dp) // unified condensed padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT COLUMN: Title & Metrics
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activity.name,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp, // Increased title size back to 18sp
                                color = AppTheme.colors.onSurface // High contrast title
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (activity.goal.isSet()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Goal(activity.type.getLabel(activity.goal.value).value())
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Today's block (index 0) is slightly wider for emphasis
                        MetricBlock(
                            data = item.past[0],
                            alpha = 0.35f,
                            width = 56.dp,
                            metricStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        )
                        // Previous 4 days
                        MetricBlock(data = item.past[1], alpha = 0.35f)
                        MetricBlock(data = item.past[2], alpha = 0.35f)
                        MetricBlock(data = item.past[3], alpha = 0.35f)
                        MetricBlock(data = item.past[4], alpha = 0.35f)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT COLUMN: Action Button
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
                                datetime_start = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                datetime_end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            )

                            Type.SCORE -> TrackedActivityScore(
                                activity_id = activity.id,
                                datetime_completed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                score = 1
                            )

                            Type.CHECKED -> null
                        }

                        record?.let { onAddRecord(it) }
                    }
                )
            }

            // EXTENDED VIEWS (In Session & Challenge)
            AnimatedVisibility(
                visible = item.activity.isInSession(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (activity.timer != null) {
                                Text(
                                    text = stringResource(id = R.string.activity_in_timer) + " " + activity.type.getLabel(
                                        activity.timer?.toLong() ?: 0L
                                    ).value(),
                                    color = AppTheme.colors.error, // Visually appealing dark red
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.activity_in_session) + " " + item.activity.inSessionSince!!.toJavaLocalDateTime().format(
                                        DateTimeFormatter.ofPattern("HH:mm")
                                    ),
                                    color = AppTheme.colors.error,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
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

            if (item.activity.challenge.isSet()) {
                Box(
                    modifier = Modifier.padding(top = 8.dp)
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
fun ActionButton(
    modifier: Modifier = Modifier,
    actionButton: ActionButton,
    activity: TrackedActivity,
    onClick: (() -> Unit),
    onLongClick: (() -> Unit) = {},
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(46.dp) // Adjusted Action button size proportionally
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

        // Clean, aesthetic colors
        val bgColor = if (actionButton == ActionButton.IN_SESSION) AppTheme.colors.errorContainer else AppTheme.colors.iconBackground
        val iconColor = if (actionButton == ActionButton.IN_SESSION) AppTheme.colors.onPrimary else AppTheme.colors.primary

        val glow = remember {
            mutableStateOf(42.dp)
        }

        if (actionButton == ActionButton.IN_SESSION) LaunchedEffect(activity.inSessionSince) {
            while (currentCoroutineContext().isActive) {
                glow.value = if (glow.value == 42.dp) 46.dp else 42.dp
                delay(1000)
            }
        }

        Box(
            Modifier
                .size(glow.value)
                .background(bgColor, CircleShape), contentAlignment = Alignment.Center
        ) {
            Icon(
                contentDescription = null,
                imageVector = icon,
                tint = iconColor,
                modifier = Modifier.size(22.dp) // Slightly bigger icon
            )
        }
    }
}


@Composable
fun Goal(label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(50),
        color = AppTheme.colors.surfaceVariant.copy(alpha = 0.6f), // Clean translucent grey outline
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Outlined.Flag,
                tint = AppTheme.colors.onSurfaceVariant,
                contentDescription = null
            )

            Text(
                label,
                Modifier.padding(start = 4.dp),
                textAlign = TextAlign.Center,
                color = AppTheme.colors.onSurface, // Strong text
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackedActivityTimeInSession() = AppTheme {
    val activity = TrackedActivity(
        id = 1L,
        name = "Reading",
        type = Type.TIME,
        goal = TrackedActivityGoal(3600L, TimeRange.DAILY),
        challenge = TrackedActivityChallenge.empty,
        inSessionSince = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )

    // Using COMPLETED (success) and DEFAULT combinations
    val item = TrackedActivityRecentOverview(
        activity = activity,
        challengeMetric = 0L,
        past = listOf(
            MetricWidgetData({ "1h" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "30m" }, status = MetricStatus.DEFAULT),
            MetricWidgetData({ "0m" }, status = MetricStatus.DEFAULT),
            MetricWidgetData({ "2h" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "1h" }, status = MetricStatus.COMPLETED)
        ),
        actionButton = ActionButton.IN_SESSION,
        today = MetricAggregation(
            from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            metric = 3600L
        )
    )

    AppTheme {
        TrackedActivity(
            item = item,
            onNavigate = {},
            onActionButtonClick = {},
            onAddRecord = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackedActivityScore() = AppTheme {
    val activity = TrackedActivity(
        id = 2L,
        name = "Pushups",
        type = Type.SCORE,
        goal = TrackedActivityGoal(100L, TimeRange.DAILY),
        challenge = TrackedActivityChallenge.empty,
    )

    // Using COMPLETED (success) and NOT_COMPLETED (failure) combinations
    val item = TrackedActivityRecentOverview(
        activity = activity,
        challengeMetric = 0L,
        past = listOf(
            MetricWidgetData({ "50" }, status = MetricStatus.NOT_COMPLETED),
            MetricWidgetData({ "100" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "120" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "80" }, status = MetricStatus.NOT_COMPLETED),
            MetricWidgetData({ "100" }, status = MetricStatus.COMPLETED)
        ),
        actionButton = ActionButton.DEFAULT,
        today = MetricAggregation(
            from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            metric = 50L
        )
    )

    AppTheme {
        TrackedActivity(
            item = item,
            onNavigate = {},
            onActionButtonClick = {},
            onAddRecord = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackedActivityChecked() = AppTheme {
    val activity = TrackedActivity(
        id = 3L,
        name = "Meditation",
        type = Type.CHECKED,
        goal = TrackedActivityGoal(1L, TimeRange.DAILY),
        challenge = TrackedActivityChallenge.empty,
    )

    // Using COMPLETED (success) and DEFAULT combinations
    val item = TrackedActivityRecentOverview(
        activity = activity,
        challengeMetric = 0L,
        past = listOf(
            MetricWidgetData({ "YES" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "NO" }, status = MetricStatus.DEFAULT),
            MetricWidgetData({ "YES" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "YES" }, status = MetricStatus.COMPLETED),
            MetricWidgetData({ "NO" }, status = MetricStatus.DEFAULT)
        ),
        actionButton = ActionButton.CHECKED,
        today = MetricAggregation(
            from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            metric = 1L
        )
    )

    AppTheme {
        TrackedActivity(
            item = item,
            onNavigate = {},
            onActionButtonClick = {},
            onAddRecord = {}
        )
    }
}
