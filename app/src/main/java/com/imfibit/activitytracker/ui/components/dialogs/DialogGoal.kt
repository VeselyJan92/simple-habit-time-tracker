package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
inline fun DialogGoal(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
    noinline onGoalSet: (Long)->Unit
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = stringResource(id = R.string.dialog_goal_title))

        var goal by remember { mutableStateOf(activity.goal.value) }

        when(activity.type){
            TrackedActivity.Type.TIME ->{
                MinuteAndHourSelector(
                    hours = goal.toInt()/3600,
                    minutes = ((goal % 3600)/60L).toInt()
                ){
                    hours, minutes -> goal = (60 * hours + minutes) * 60L
                }
            }

            TrackedActivity.Type.SCORE -> {
                NumberSelector(
                        label = stringResource(id = R.string.score),
                        number = mutableStateOf(goal.toInt())
                ) {
                    if (goal in 1..10_000)
                        goal = it.toLong()
                }
            }
            TrackedActivity.Type.CHECKED ->{
                NumberSelector(
                    label = stringResource(id = R.string.repetitions),
                    number = mutableStateOf(goal.toInt())
                ){
                    when(activity.goal.range){
                        TimeRange.DAILY -> throw IllegalStateException()
                        TimeRange.WEEKLY -> if (it in 0..7) goal = it.toLong()
                        TimeRange.MONTHLY -> if (it in 0..31) goal = it.toLong()
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

            TextButton(onClick = {display.value = false ; onGoalSet.invoke(goal)}) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}