package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.layout.LabeledColumn
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime


@Composable
fun Timer(
    startTime: LocalDateTime?,
) {
    val startTime = when {
        startTime == null -> null
        startTime > LocalDateTime.now() -> null
        else -> startTime
    }

    var time by remember { mutableStateOf(LocalDateTime.now()) }


    LaunchedEffect(Unit) {
        while (this.coroutineContext.isActive) {
            time = LocalDateTime.now()
            delay(1000)
        }
    }

    val text = if (startTime != null) {
        TimeUtils.secondsToMetric(startTime, time)
    } else {
        TimeUtils.secondsToMetric(0L)
    }

    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    )
}


@Composable
fun TimerBlock(
    modifier: Modifier = Modifier,
    startTime: LocalDateTime?,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(Colors.ChipGray, RoundedCornerShape(10.dp))
            .padding(4.dp)
            .clickable(onClick = {
                onClick?.invoke()
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Timer, contentDescription = null)

        Spacer(modifier = Modifier.width(4.dp))

        Timer(startTime)
    }
}


@Composable
private fun DialogSetTimerStart(
    display: MutableState<Boolean>,
    startTime: LocalDateTime,
    onStartSet: (LocalDateTime) -> Unit,
) {
    BaseDialog(display = display) {
        DialogBaseHeader(title = stringResource(id = R.string.dialog_timer_start_title))

        var datetime by remember { mutableStateOf(startTime) }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            LabeledColumn(text = stringResource(id = R.string.session_start)) {
                EditableDatetime(
                    datetime = datetime,
                    onDatetimeEdit = {
                        if (it < LocalDateTime.now())
                            datetime = it
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            LabeledColumn(text = stringResource(id = R.string.timer)) {
                TimerBlock(
                    startTime = datetime
                )
            }
        }

        DialogButtons {

            TextButton(onClick = { display.value = false }) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = { display.value = false; onStartSet.invoke(datetime) }) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}