package com.janvesely.activitytracker.ui.activities.composable

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.Typography

@ExperimentalLayout
@Preview
@Composable
fun ActivitiesScreen(){


    Scaffold(
        topBar = {
            TopAppBar() {
                Text("Aktivity", Modifier.gravity(Alignment.CenterVertically).padding(start = 8.dp),style = Typography.AppTitle)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {

            }

        },
        bodyContent = {
            Temp()

        },
        bottomBar = {
            LiveActivitiesList()
        },
        backgroundColor = Colors.AppBackground
    )

}