package com.imfibit.activitytracker.ui.components

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrackerTopAppBar(title: String, actions: @Composable() (() -> Unit)? = null){
    TopAppBar(backgroundColor = Colors.AppPrimary) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
            style = Typography.AppTitle
        )

        Row(
            modifier =   Modifier.align(Alignment.CenterVertically).padding(end = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions?.invoke()
        }
    }
}