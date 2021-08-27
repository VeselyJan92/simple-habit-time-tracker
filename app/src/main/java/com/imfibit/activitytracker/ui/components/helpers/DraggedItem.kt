package com.imfibit.activitytracker.ui.components.helpers

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex


fun Modifier.draggedItem(
    offset: Float?,
    orientation: Orientation = Orientation.Vertical,
): Modifier = composed {
    Modifier
        .zIndex(offset?.let { 1f } ?: 0f)
        .graphicsLayer {
            with(offset ?: 0f) {
                if (orientation == Orientation.Vertical) {
                    translationY = this
                } else {
                    translationX = this
                }
            }
        }
}