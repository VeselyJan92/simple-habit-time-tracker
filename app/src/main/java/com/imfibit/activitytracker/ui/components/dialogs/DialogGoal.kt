package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
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

        DialogBaseHeader(title = "Pick a goal")

        val goal = remember { mutableStateOf(activity.goal.value) }

        when(activity.type){
            TrackedActivity.Type.SESSION ->{
                MinuteAndHourSelector(
                    hours = mutableStateOf(goal.value.toInt()/3600),
                    minutes = mutableStateOf(((goal.value % 3600)/60L).toInt())
                ){
                    hours, minutes -> goal.value = (60 * hours + minutes) * 60L
                }
            }

            TrackedActivity.Type.SCORE -> {
                NumberSelector(
                    label = "Score",
                    number = mutableStateOf(goal.value.toInt())
                ){
                    goal.value = it.toLong()
                }
            }
            TrackedActivity.Type.CHECKED ->{
                NumberSelector(
                    label = "Score",
                    number = mutableStateOf(goal.value.toInt())
                ){
                    when(activity.goal.range){
                        TimeRange.DAILY -> throw IllegalStateException()
                        TimeRange.WEEKLY -> if (it in 0..7) goal.value = it.toLong()
                        TimeRange.MONTHLY -> if (it in 0..31) goal.value = it.toLong()
                    }
                    goal.value = it.toLong()
                }
            }

        }


        DialogButtons {
            TextButton(onClick = {display.value = false ; onGoalSet.invoke(0L)} ) {
                Text(text = "SMAZAT")
            }

            TextButton(onClick = {display.value = false} ) {
                Text(text = "ZPĚT")
            }

            TextButton(onClick = {display.value = false ; onGoalSet.invoke(goal.value)}) {
                Text(text = "POKRAČOVAT")
            }
        }
    }

}