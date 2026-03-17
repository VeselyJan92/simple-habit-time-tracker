package com.imfibit.activitytracker.ui.components.topBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.ui.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackEditableTopBar(
    onNavigateBack: () -> Unit,
    onActionClick: () -> Unit = {},
    name: String,
    onTextChanged: (String) -> Unit,
    actionIcon: ImageVector? = null,
    endIcon: (@Composable () -> Unit)? = null,
) {
    TopAppBar(
        title = {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTag.TRACKED_ACTIVITY_EDIT_NAME),
                value = name,
                singleLine = true,
                onValueChange = {
                    onTextChanged(it)
                },
                textStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 24.sp, color = AppTheme.colors.onSurface)
            )
        },
        navigationIcon = {
            TopBarBackButton(onBack = onNavigateBack)
        },
        actions = {
            if (endIcon != null) {
                endIcon()
            } else if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(imageVector = actionIcon, tint = AppTheme.colors.onSurfaceVariant, contentDescription = null)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}
