package com.imfibit.activitytracker.ui.components.topBar

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.imfibit.activitytracker.core.TestTag

@Composable
fun TopBarBackButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    IconButton(
        onClick = onBack,
        modifier = modifier
            .clickable(onClick = onBack)
            .testTag(TestTag.GENERAL_BACK_BUTTON),
    ){
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
    }
}