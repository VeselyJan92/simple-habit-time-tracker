package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity

@ExperimentalLayout
@Composable
fun ActivitiesScreen(
    navController: NavController,
    vm: ActivitiesViewModel
){
    val display = remember { mutableStateOf(false) }

    DialogAddActivity(navController, display = display)

    Scaffold(
        topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { display.value = true }) {
                Icon(asset = Icons.Filled.Add)
            }
        },
        bodyContent = {
            TrackedActivitiesList(navController, vm)
        },
        bottomBar = {
            LiveActivitiesList(vm)
        },
        backgroundColor = Colors.AppBackground
    )
}