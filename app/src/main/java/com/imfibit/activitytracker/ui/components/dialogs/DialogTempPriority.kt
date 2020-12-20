package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
inline fun DialogTempPriority(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = stringResource(id = R.string.dialog_priority_title))

        val state = mutableStateOf(activity.position)

        NumberSelector(
                label = stringResource(id = R.string.priority),
                number = state
        ){
            state.value = it
        }

        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel ))
            }

            TextButton(
                onClick = {
                    display.value = false
                    GlobalScope.launch {
                        AppDatabase.db.activityDAO.update(activity.copy(position = state.value))
                    }

                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue ))
            }
        }
    }

}