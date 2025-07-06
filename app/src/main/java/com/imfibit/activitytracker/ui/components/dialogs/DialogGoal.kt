package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector

@Preview
@Composable
fun DialogGoal_PreviewTime() = AppTheme {
    DialogGoal(
        onDismissRequest = {},
        activity = DevSeeder.getTrackedActivityTime(),
        onGoalSet = { }
    )
}

@Preview
@Composable
fun DialogGoal_PreviewScore() = AppTheme {
    DialogGoal(
        onDismissRequest = {},
        activity = DevSeeder.getTrackedActivityScore(),
        onGoalSet = { }
    )
}

@Preview
@Composable
fun DialogGoal_PreviewChecked() = AppTheme {
    DialogGoal(
        onDismissRequest = {},
        activity = DevSeeder.getTrackedActivityCompletion(
            goal = TrackedActivityGoal(0, TimeRange.WEEKLY),
        ),
        onGoalSet = { }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogGoal(
    onDismissRequest: () -> Unit,
    activity: TrackedActivity,
    onGoalSet: (Long) -> Unit,
) = BaseDialog(onDismissRequest = onDismissRequest) {

    DialogBaseHeader(title = stringResource(id = R.string.dialog_goal_title))

    var goal by remember { mutableStateOf(activity.goal.value) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

        when (activity.type) {
            TrackedActivity.Type.TIME -> {
                MinuteAndHourSelector(
                    hours = goal.toInt() / 3600,
                    minutes = ((goal % 3600) / 60L).toInt(),
                    onSelectionChanged = { hours, minutes ->
                        goal = (60 * hours + minutes) * 60L
                    }
                )
            }

            TrackedActivity.Type.SCORE -> {
                NumberSelector(
                    label = stringResource(id = R.string.score),
                    number = goal.toInt(),
                    range = 0..1000
                ) {
                    goal = it.toLong()
                }
            }

            TrackedActivity.Type.CHECKED -> {
                NumberSelector(
                    label = stringResource(id = R.string.repetitions),
                    number = goal.toInt(),
                    range = when (activity.goal.range) {
                        TimeRange.DAILY -> throw IllegalStateException()
                        TimeRange.WEEKLY -> 0..7
                        TimeRange.MONTHLY -> 0..31
                    }
                ) {
                    goal = it.toLong()
                }
            }
        }
    }

    DialogButtons {
        TextButton(
            onClick = {
                onDismissRequest()
                onGoalSet.invoke(0L)
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_delete))
        }

        TextButton(
            onClick = onDismissRequest
        ) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                onDismissRequest()
                onGoalSet.invoke(goal)
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}