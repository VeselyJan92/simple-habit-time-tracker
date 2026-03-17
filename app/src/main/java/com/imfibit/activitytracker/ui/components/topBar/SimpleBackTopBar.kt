package com.imfibit.activitytracker.ui.components.topBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackTopBar(
    modifier: Modifier = Modifier,
    title: String,
    endIcon: @Composable () -> Unit = { },
    onBack: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = { TopBarTitle(title = title) },
        navigationIcon = { TopBarBackButton(onBack = onBack) },
        actions = { endIcon() },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}