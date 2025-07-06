package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Score
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.ScrollBottomSheet
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddActivityBottomSheet_Preview() = AppTheme {
    AddActivityBottomSheet(
        state = rememberTestBottomSheetState(),
        onDismissRequest = {},
        onAddActivity = {},
        onAddFolder = {},
        onAddFocusItem = {},
        onAddDailyChecklist = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityBottomSheet(
    state: SheetState,
    onDismissRequest: () -> Unit,
    onAddActivity: (type: TrackedActivity.Type) -> Unit,
    onAddFolder: () -> Unit,
    onAddFocusItem: () -> Unit,
    onAddDailyChecklist: () -> Unit,
) {
    ScrollBottomSheet(
        state = state,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier

                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            SectionTitle(text = "Habit")

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_TIME),
                icon = Icons.Default.Timer,
                name = stringResource(id = R.string.new_activity_time),
                clickable = { onAddActivity(TrackedActivity.Type.TIME) }
            )

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_SCORE),
                icon = Icons.Default.Score,
                name = stringResource(id = R.string.new_activity_score),
                clickable = { onAddActivity(TrackedActivity.Type.SCORE) }
            )

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_COMPLETION),
                icon = Icons.Default.AssignmentTurnedIn, // Corrected icon parameter name
                name = stringResource(id = R.string.new_activity_checked),
                clickable = { onAddActivity(TrackedActivity.Type.CHECKED) }
            )

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_GROUP),
                icon = Icons.Default.Topic,
                name = stringResource(id = R.string.new_activity_folder),
                clickable = onAddFolder
            )

            // Added Divider and spacing for better visual separation
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SectionTitle(text = "Focus board")

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_FOCUS_ITEM),
                icon = Icons.AutoMirrored.Filled.Assignment,
                name = stringResource(id = R.string.new_activity_focus_item),
                clickable = onAddFocusItem
            )

            // Added Divider and spacing for better visual separation
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SectionTitle(text = "Daily Checklist")

            TrackedActivities(
                modifier = Modifier.testTag(TestTag.DIALOG_ADD_CHECKLIST_ITEM),
                icon = Icons.Default.Checklist,
                name = stringResource(id = R.string.new_activity_daily_checklist_item),
                clickable = onAddDailyChecklist
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium, // Using MaterialTheme typography
        modifier = Modifier.padding(vertical = 8.dp) // Added padding to section titles
    )
}


@Composable
private fun TrackedActivities(
    modifier: Modifier = Modifier, // Provide a default value for modifier
    icon: ImageVector,
    name: String,
    clickable: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = clickable),
        shape = RoundedCornerShape(10.dp),
        color = Colors.ChipGray
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}