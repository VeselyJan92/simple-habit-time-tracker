package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.IconButton
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Preview
@Composable
fun DialogTimers_Preview() = AppTheme() {
    DialogTimers(
        onDismissRequest = {},
        activity = DevSeeder.getTrackedActivityTime(),
        timers = listOf(
            DevSeeder.getPresetTimer(id = 1),
            DevSeeder.getPresetTimer(id = 2)
        ),
        onTimerDelete = {},
        onTimerAdd = {},
        swapTimer = { _, _ -> },
        runTimer = {}
    )
}

@Composable
fun DialogTimers(
    onDismissRequest: () -> Unit,
    activity: TrackedActivity,
    timers: List<PresetTimer>,
    onTimerDelete: ((timer: PresetTimer) -> Unit),
    onTimerAdd: ((timer: PresetTimer) -> Unit),
    swapTimer: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    runTimer: ((timer: PresetTimer) -> Unit),
) = BaseDialog(onDismissRequest = onDismissRequest) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DialogBaseHeader(title = stringResource(R.string.dialog_preset_timers_title))

        Spacer(modifier = Modifier.padding(8.dp))

        var add by remember { mutableStateOf(false) }

        if (add) {
            DialogTimePicker(
                onDismissRequest = { add = false },
                initialHour = 1,
                initialMinute = 0,
                onTimePicked = { hour, minute ->
                    onTimerAdd(PresetTimer(0, activity.id, hour * 3600 + minute * 60, 0))
                }
            )
        }


        IconButton(
            text = stringResource(R.string.dialog_preset_timers_add), icon = Icons.Filled.AlarmAdd,
            onClick = { add = true }
        )
    }


    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            swapTimer(from, to)
        }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        item {
            var show by remember { mutableStateOf(false) }

            if (show) {
                DialogTimePicker(
                    onDismissRequest = { show = false },
                    initialHour = 1,
                    initialMinute = 0,
                    onTimePicked = { hour, minute ->
                        runTimer(PresetTimer(0, activity.id, hour * 3600 + minute * 60, 0))
                    }
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.SuperLight, RoundedCornerShape(30))
                    .padding(16.dp)
                    .clickable {
                        show = true
                    },
                text = stringResource(R.string.dialog_preset_timers_single),
                style = TextStyle(
                    fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            )
        }


        items(
            items = timers,
            key = { item -> item.id },
        ) { item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = item.id
            ) {
                val dismissState = rememberSwipeToDismissBoxState()

                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                    onTimerDelete.invoke(item)
                }

                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier
                        .clickable { runTimer.invoke(item) }
                        .draggableHandle(),
                    backgroundContent = { },
                    content = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Colors.SuperLight, RoundedCornerShape(30))
                                .padding(16.dp),
                            text = TimeUtils.secondsToMetric(item.seconds.toLong())
                                .removeSuffix(":00"),
                            style = TextStyle(
                                fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )
            }
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

