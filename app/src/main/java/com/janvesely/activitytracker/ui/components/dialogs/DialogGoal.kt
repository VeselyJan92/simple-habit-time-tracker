package com.janvesely.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.ExperimentalFocus
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.janvesely.activitytracker.ui.components.selectors.NumberSelector


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
inline fun DialogGoal(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
    noinline onGoalSet: (Long)->Unit
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = "Pick a goal")

        val goal = remember { mutableStateOf(activity.expected) }

        when(activity.type){
            TrackedActivity.Type.SESSION ->{
                MinuteAndHourSelector(
                    hours = mutableStateOf(goal.value.toInt()/3600),
                    minutes = mutableStateOf(((goal.value % 3600)/60L).toInt())
                ){
                    hours, minutes -> goal.value = (60 * hours + minutes*60).toLong()
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
            TrackedActivity.Type.COMPLETED -> throw IllegalStateException()
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