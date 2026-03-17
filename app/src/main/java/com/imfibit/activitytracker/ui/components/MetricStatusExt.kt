package com.imfibit.activitytracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.imfibit.activitytracker.core.enums.MetricStatus
import com.imfibit.activitytracker.ui.AppTheme

val MetricStatus.color: Color
    @Composable
    get() = when (this) {
        MetricStatus.COMPLETED -> AppTheme.colors.statusCompleted
        MetricStatus.NOT_COMPLETED -> AppTheme.colors.statusNotCompleted
        MetricStatus.DEFAULT -> AppTheme.colors.statusNeutral
        MetricStatus.ACCENT -> AppTheme.colors.appAccent
        MetricStatus.NONE -> Color.Transparent
    }
