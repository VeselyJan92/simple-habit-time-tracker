package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.IconButton
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import org.burnoutcrew.reorderable.*
import java.time.LocalTime


/*fun Modifier.draggedItemx(
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
}*/

@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun DialogTimers(
        display: MutableState<Boolean> = mutableStateOf(true),
        activity: TrackedActivity,
        timers: MutableList<PresetTimer>,
        noinline onTimerDelete: ((timer: PresetTimer)->Unit),
        noinline onTimerAdd: ((timer: PresetTimer)->Unit),
        noinline onTimersReorganized: ((timers: List<PresetTimer>)->Unit),
        noinline runTimer: ((timer: PresetTimer)->Unit)
)  = BaseDialog(display = display) {

    DialogBaseHeader(title = stringResource( R.string.dialog_preset_timers_title))

    Row(
        modifier = Modifier
            .padding(top = 16.dp, start = 12.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        val context = LocalContext.current

        IconButton(text = stringResource( R.string.dialog_preset_timers_single), icon = Icons.Default.Alarm ) {
            DialogTimePicker(
                time = LocalTime.MIDNIGHT,
                onTimeSet = {
                    runTimer.invoke(
                        PresetTimer(
                            0,
                            activity.id,
                            it.hour * 3600 + it.minute * 60,
                            0
                        )
                    )
                },
                context = context
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        IconButton(text = stringResource( R.string.dialog_preset_timers_add), icon = Icons.Filled.AlarmAdd ) {
            DialogTimePicker(
                time = LocalTime.MIDNIGHT,
                onTimeSet = {
                    onTimerAdd.invoke(
                        PresetTimer(
                            0,
                            activity.id,
                            it.hour * 3600 + it.minute * 60,
                            0
                        )
                    )
                    display.value = false
                },
                context = context
            )
        }
    }

    Text(text = stringResource( R.string.dialog_preset_timers_preset), fontWeight = FontWeight.W500, fontSize = 20.sp, modifier = Modifier.padding(8.dp))


    val state: ReorderableState = rememberReorderState(

    )


    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .reorderable(
                state = state,
                onDragEnd = { from, to -> onTimersReorganized.invoke(timers)},
                onMove = { from, to -> timers.move(from, to) })
    ){

        itemsIndexed(timers){ index, item ->

            val dismissState = remember(item) {
                DismissState(DismissValue.Default)
            }

            if (dismissState.isDismissed(DismissDirection.EndToStart)){
                onTimerDelete.invoke(item)
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier
                    .draggedItem(state.offsetByIndex(index))
                    .clickable { runTimer.invoke(item) },
                background = {},
            ) {
                Row(modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(30))
                    .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .background(Colors.ChipGray, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = TimeUtils.secondsToMetric(item.seconds.toLong()).removeSuffix(":00"),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                }

            }

        }

    }
}
