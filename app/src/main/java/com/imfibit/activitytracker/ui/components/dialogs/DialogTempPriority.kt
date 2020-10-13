package com.janvesely.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.ExperimentalFocus
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.janvesely.activitytracker.ui.components.selectors.NumberSelector
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
inline fun DialogTempPriority(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = "Priority")

        val state = mutableStateOf(activity.position)

        NumberSelector(
            label = "Priority",
            number = state
        ){
            state.value = it
        }


        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = "ZPĚT")
            }

            TextButton(
                onClick = {
                    display.value = false
                    GlobalScope.launch {
                        AppDatabase.db.activityDAO.update(activity.copy(position = state.value))
                    }

                }
            ) {
                Text(text = "POKRAČOVAT")
            }
        }
    }

}