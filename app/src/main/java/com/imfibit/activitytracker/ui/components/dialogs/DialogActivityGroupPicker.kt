package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors


@Preview
@Composable
fun DialogActivityGroupPicker_Preview() = AppTheme {
    DialogActivityGroupPicker(
        onDismissRequest = {},
        activity = DevSeeder.getTrackedActivityTime(),
        groups = listOf(
            DevSeeder.getActivityGroup(name = "Group 1"),
            DevSeeder.getActivityGroup(name = "Group 1"),
        ),
        select = {}
    )
}

@Composable
fun DialogActivityGroupPicker(
    onDismissRequest: () -> Unit,
    activity: TrackedActivity,
    groups: List<TrackerActivityGroup>,
    select: ((group: TrackerActivityGroup?) -> Unit),
) = BaseDialog(
    onDismissRequest = onDismissRequest
) {
    DialogBaseHeader(title = stringResource(R.string.dialog_group_picker_title))

    LazyColumn(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        itemsIndexed(groups) { index, group ->
            Group(activity = activity, group = group, select = select)
        }

        item {
            Group(activity = activity, group = null, select = select)
        }
    }

    DialogButtons {
        TextButton(
            onClick = onDismissRequest
        ) {
            Text(text = "OK")
        }
    }
}

@Composable
private fun Group(
    activity: TrackedActivity,
    group: TrackerActivityGroup?,
    select: ((group: TrackerActivityGroup?) -> Unit),
) {
    val color = if (activity.groupId == group?.id) Colors.ChipGraySelected else Colors.ChipGray

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { select(group) })
            .background(color, RoundedCornerShape(8.dp))
            .padding(12.dp),
        text = group?.name ?: stringResource(id = R.string.dialog_group_picker_default),
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    )
}
