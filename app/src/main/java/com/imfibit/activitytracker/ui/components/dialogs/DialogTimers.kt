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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.IconButton
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalTime

@Composable
inline fun DialogTimers(
    display: MutableState<Boolean> = mutableStateOf(true),
    activity: TrackedActivity,
    timers: List<PresetTimer>,
    noinline onTimerDelete: ((timer: PresetTimer) -> Unit),
    noinline onTimerAdd: ((timer: PresetTimer) -> Unit),
    noinline swapTimer: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    noinline runTimer: ((timer: PresetTimer) -> Unit),
) = BaseDialog(display = display) {

    DialogBaseHeader(title = stringResource(R.string.dialog_preset_timers_title))

    Row(
        modifier = Modifier
            .padding(top = 16.dp, start = 12.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        val context = LocalContext.current

        IconButton(
            text = stringResource(R.string.dialog_preset_timers_single), icon = Icons.Default.Alarm
        ) {
            DialogTimePicker(
                time = LocalTime.MIDNIGHT, onTimeSet = {
                    runTimer.invoke(
                        PresetTimer(
                            0, activity.id, it.hour * 3600 + it.minute * 60, 0
                        )
                    )
                }, context = context
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        IconButton(
            text = stringResource(R.string.dialog_preset_timers_add), icon = Icons.Filled.AlarmAdd
        ) {
            DialogTimePicker(
                time = LocalTime.MIDNIGHT, onTimeSet = {
                    onTimerAdd.invoke(
                        PresetTimer(
                            0, activity.id, it.hour * 3600 + it.minute * 60, 0
                        )
                    )
                    display.value = false
                }, context = context
            )
        }
    }

    Text(
        text = stringResource(R.string.dialog_preset_timers_preset),
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        modifier = Modifier.padding(8.dp)
    )

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            swapTimer(from, to)
        }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)

    ) {
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
                        Row(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                                .background(
                                    Color.LightGray, RoundedCornerShape(30)
                                )
                                .padding(4.dp), verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(30.dp)
                                    .background(Colors.ChipGray, RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = TimeUtils.secondsToMetric(item.seconds.toLong())
                                        .removeSuffix(":00"),
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 14.sp, fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                        }


                    }
                )
            }
        }
    }
}
