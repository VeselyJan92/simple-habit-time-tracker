package com.imfibit.activitytracker.ui.components.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun TestableContent(testTag: String, modifier: Modifier = Modifier, content: @Composable BoxScope.()->Unit ) = Box(
    modifier = modifier.testTag(testTag),
    content = content
)
