package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors


@Composable
fun DialogActivityGroupPicker(
    display: MutableState<Boolean> = mutableStateOf(true),
    activity: TrackedActivity,
    groups: List<TrackerActivityGroup>,
    select: ((group: TrackerActivityGroup?)->Unit),
) = BaseDialog(display = display) {

    DialogBaseHeader(title = stringResource( R.string.dialog_group_picker_title))

    LazyColumn(
        modifier = Modifier.padding(8.dp, 8.dp, 8.dp )
    ){

        itemsIndexed(groups) { index, group ->
            Group(activity = activity, group = group, select = select)
        }

        item {
            Group(activity = activity, group = null, select = select)
        }
    }
}

@Composable
private fun Group(activity: TrackedActivity, group: TrackerActivityGroup?, select: ((group: TrackerActivityGroup?)->Unit)){
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(30))
            .padding(4.dp)
            .clickable { select(group) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        val color = if (activity.groupId == group?.id) Colors.ChipGraySelected else Colors.ChipGray

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(30.dp)
                .background(color, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = group?.name ?: stringResource(id = R.string.dialog_group_picker_default),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

    }

}
