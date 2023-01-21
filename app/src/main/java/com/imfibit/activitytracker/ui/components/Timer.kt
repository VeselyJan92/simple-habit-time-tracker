package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
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
    modifier: Modifier  = Modifier
        .size(130.dp, 25.dp),
    startTime: LocalDateTime?,
    enable: Boolean = true,
    onClick: ((LocalDateTime) -> Unit)? = null,
){

    val display = remember { mutableStateOf(false)}


    val startTime = when {
        startTime == null -> null
        startTime > LocalDateTime.now() -> null
        else -> startTime
    }

    var time  by remember { mutableStateOf(LocalDateTime.now()) }

    if (enable){
        LaunchedEffect(time) {
            while (this.coroutineContext.isActive) {
                time = LocalDateTime.now()
                delay(1000)
            }
        }
    }

    Row(
        modifier = Modifier
            .then(modifier)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(onClick = { display.value = true }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Modifier
            .align(Alignment.CenterVertically)
            .padding(start = 5.dp)
            .size(20.dp)
        Icon(Icons.Filled.Timer, contentDescription = null)


        val text = if(startTime != null){
            TimeUtils.secondsToMetric(startTime, time)
        }else{
            TimeUtils.secondsToMetric(0L)
        }


        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun DialogSetTimerStart(
    display: MutableState<Boolean>,
    startTime: LocalDateTime,
    onStartSet: (LocalDateTime)->Unit
){
    BaseDialog(display = display ) {
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
                Timer(
                    modifier = Modifier
                        .size(130.dp, height = 30.dp)
                        .padding(end = 8.dp),
                    startTime = datetime
                )
            }
        }

        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = {display.value = false ; onStartSet.invoke(datetime)}) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}