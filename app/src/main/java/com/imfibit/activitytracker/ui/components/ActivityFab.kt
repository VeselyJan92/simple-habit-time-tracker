package com.imfibit.activitytracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme

private data class ActivityOption(
    val labelStringRes: Int,
    val type: TrackedActivity.Type,
    val icon: ImageVector,
)

@Composable
fun ActivityOptions(
    onOptionSelected: (TrackedActivity.Type) -> Unit,
    onGroupSelected: (() -> Unit)? = null,
) {
    listOf(
        ActivityOption(R.string.dashboard_fab_time, TrackedActivity.Type.TIME, Icons.Default.Timer),
        ActivityOption(R.string.dashboard_fab_score, TrackedActivity.Type.SCORE, Icons.Default.SportsScore),
        ActivityOption(R.string.dashboard_fab_check, TrackedActivity.Type.CHECKED, Icons.Default.CheckBox)
    ).forEach { option ->
        ExtendedFloatingActionButton(
            onClick = { onOptionSelected(option.type) },
            text = { Text(stringResource(id = option.labelStringRes)) },
            icon = { Icon(option.icon, contentDescription = null) },
            containerColor = AppTheme.colors.outlineVariant,
            contentColor = AppTheme.colors.onSurface
        )
    }

    if (onGroupSelected != null) {
        ExtendedFloatingActionButton(
            onClick = onGroupSelected,
            text = { Text(stringResource(id = R.string.dashboard_fab_folder)) },
            icon = { Icon(Icons.Default.Folder, contentDescription = null) },
            containerColor = AppTheme.colors.outlineVariant,
            contentColor = AppTheme.colors.onSurface
        )
    }
}

@Composable
fun ActivityFab(
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    onOptionSelected: (TrackedActivity.Type) -> Unit,
    modifier: Modifier = Modifier,
    onGroupSelected: (() -> Unit)? = null,
) {
    val rotation by animateFloatAsState(if (expanded) 45f else 0f, label = "rotation")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (expanded) {
            ActivityOptions(
                onOptionSelected = {
                    onExpandedChanged(false)
                    onOptionSelected(it)
                },
                onGroupSelected = if (onGroupSelected != null) {
                    {
                        onExpandedChanged(false)
                        onGroupSelected()
                    }
                } else null
            )
        }

        FloatingActionButton(
            onClick = { onExpandedChanged(!expanded) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(id = R.string.dashboard_fab_add),
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}
