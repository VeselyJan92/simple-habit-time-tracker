package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity

@ExperimentalLayout
@Composable
fun ScreenActivities(
    navController: NavHostController,
){

    val vm = viewModel<ActivitiesViewModel>()
    val display = remember { mutableStateOf(false) }

    DialogAddActivity(navController, display = display)

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)) {
                IconButton(
                    onClick = {
                        navController.navigate("settings")
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Settings)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { display.value = true }) {
                Icon(Icons.Filled.Add)
            }
        },
        bodyContent = {
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