package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector


@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun DialogGoal(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
    noinline onGoalSet: (Long)->Unit
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = stringResource(id = R.string.dialog_goal_title))

        val goal = remember { mutableStateOf(activity.goal.value) }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

            when(activity.type){
                TrackedActivity.Type.TIME ->{
                    MinuteAndHourSelector(
                        hours = goal.value.toInt()/3600,
                        minutes = ((goal.value % 3600)/60L).toInt(),
                        onSelectionChanged = {
                                hours, minutes -> goal.value = (60 * hours + minutes) * 60L
                        }
                    )
                }

                TrackedActivity.Type.SCORE -> {
                    NumberSelector(
                        label = stringResource(id = R.string.score),
                        number = goal.value.toInt(),
                        range = 0..1000
                    ) {
                        goal.value = it.toLong()
                    }
                }
                TrackedActivity.Type.CHECKED ->{
                    NumberSelector(
                        label = stringResource(id = R.string.repetitions),
                        number = goal.value.toInt(),
                        range = when(activity.goal.range){
                            TimeRange.DAILY -> throw IllegalStateException()
                            TimeRange.WEEKLY -> 0..7
                            TimeRange.MONTHLY -> 0..31
                        }
                    ){
                        goal.value = it.toLong()
                    }
                }

            }
        }


        DialogButtons {
            TextButton(onClick = {display.value = false ; onGoalSet.invoke(0L)} ) {
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }

            TextButton(onClick = {display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = {display.value = false ; onGoalSet.invoke(goal.value)}) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}