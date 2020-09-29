package com.janvesely.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.outlined.PlusOne
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.Typography

@ExperimentalLayout
@Composable
fun ActivitiesScreen(
    navController: NavController,
    vm: ActivitiesViewModel
){

    Scaffold(
        topBar = {
            TopAppBar() {
                Text(
                    "Aktivity",
                    Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                    style = Typography.AppTitle
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
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