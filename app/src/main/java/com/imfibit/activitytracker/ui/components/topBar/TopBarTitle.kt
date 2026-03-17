package com.imfibit.activitytracker.ui.components.topBar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.ui.AppTheme

@Composable
fun TopBarTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        color = AppTheme.colors.onSurface
    )
}
