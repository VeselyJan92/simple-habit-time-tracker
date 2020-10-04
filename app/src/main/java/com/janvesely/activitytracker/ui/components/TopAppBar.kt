package com.janvesely.activitytracker.ui.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrackerTopAppBar(title: String){
    TopAppBar(backgroundColor = Colors.AppPrimary) {
        Text(
            "Historie",
            Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
            style = Typography.AppTitle
        )
    }
}