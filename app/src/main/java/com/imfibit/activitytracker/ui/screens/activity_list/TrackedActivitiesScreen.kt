package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ScreenActivities(
    navController: NavHostController,
){

    val vm = hiltViewModel<ActivitiesViewModel>()
    val display = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    DialogAddActivity(display = display){
        scope.launch {

            val activity = TrackedActivity(
                id = 0L,
                name = name,
                position = 0,
                type = it,
                inSessionSince = null,
                goal = TrackedActivityGoal(0L, TimeRange.WEEKLY)
            )

            val id = withContext(Dispatchers.IO){
                vm.addActivity(activity)
            }

            withContext(Dispatchers.Main){
                display.value = false
                navController.navigate("screen_activity/$id")
            }
        }
    }

    Scaffold(
            topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)) },
            floatingActionButton = {
                FloatingActionButton(onClick = { display.value = true }) {
                    Icon(Icons.Filled.Add, null)

                }
            },
            content = {
                TrackedActivitiesList(navController, vm)
            },
            bottomBar = {
                Column {
                    LiveActivitiesList(vm)
                    AppBottomNavigation(navController)
                }
            },
            backgroundColor = Colors.AppBackground
    )
}