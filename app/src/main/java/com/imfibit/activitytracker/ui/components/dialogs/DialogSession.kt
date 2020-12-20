package com.imfibit.activitytracker.ui.components.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun DialogSession(
        display: MutableState<Boolean> = mutableStateOf(true),
        from: LocalDateTime,
        to: LocalDateTime,
        recordId: Long = 0,
        activityId: Long = 0,
        noinline onSetSession: ((LocalDateTime, LocalDateTime)->Unit)? = null
)  = BaseDialog(display = display) {

    val modify = remember {recordId != 0L}


    val x = if (modify) from else from.toLocalDate().atTime(15, 0)
    val y = if (modify) to else from.plusHours(1L)

    var from = remember { mutableStateOf(x) }
    var to = remember { mutableStateOf(y) }

    val seconds = java.time.Duration.between(from.value , to.value).seconds

    val valid = from.value < to.value && seconds <= 60*60*24

    val context = AmbientContext.current

    DialogBaseHeader(title = stringResource(id = if (modify) R.string.dialog_session_title_edit else R.string.dialog_session_title_add))

    Row(Modifier.padding(8.dp, top = 16.dp).height(50.dp), horizontalArrangement = Arrangement.SpaceBetween) {

        Column(Modifier.weight(50f).padding(end = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.session_start),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50)).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = from.value?.format(DateTimeFormatter.ofPattern("dd. MM.")) ?: "-",
                        modifier = Modifier.clickable(
                            onClick = {

                                DatePickerDialog(context,
                                    0,
                                    { _, i, i2, i3 -> from.value=  from.value!!.withYear(i).withMonth(i2).withDayOfMonth(i3)},
                                    from.value!!.year, from.value!!.month.value, from.value!!.dayOfMonth
                                ).show()
                            },
                            indication = rememberRipple(bounded = false)
                        )
                    )

                    Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

                    Text(
                        textAlign = TextAlign.Center,
                        text = from.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(
                            onClick = {
                                TimePickerDialog(context,
                                    0,
                                    { _, h, m -> from.value =  from.value.withHour(h).withMinute(m)},
                                    from.value.hour, from.value.minute, true
                                ).show()
                            },
                            indication = rememberRipple(bounded = false)
                        )
                    )
                }
            }

        }

        Column(Modifier.weight(25f).padding(end = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                textAlign = TextAlign.Center,
                text = "",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )


            Box(
                Modifier.height(30.dp)
                    .background(Colors.AppAccent, RoundedCornerShape(50))
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            TimePickerDialog(context,
                                0,
                                { _, h, m -> to.value = from.value.plusMinutes(h * 60L + m)},
                                0, 0, true
                            ).show()
                        },
                        indication = rememberRipple(bounded = false)
                    ),
                contentAlignment = Alignment.Center,

            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = if (valid) TimeUtils.secondsToMetricShort(from.value, to.value) else "-",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }

        Column(Modifier.weight(50f).padding(end = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.session_end),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )



            Box(
                modifier = Modifier
                    .height(30.dp).fillMaxWidth()
                    .background(if (from.value <= to.value) Colors.ChipGray else Colors.NotCompleted, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = to.value.format(DateTimeFormatter.ofPattern("dd. MM.")),
                        modifier = Modifier.clickable(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    0,
                                    { _, i, i2, i3 ->
                                        to.value = to.value.withYear(i).withMonth(i2).withDayOfMonth(i3)
                                    },
                                    to.value.year, to.value.month.value, to.value.dayOfMonth
                                ).show()
                            },
                            indication = rememberRipple(bounded = false)
                        )
                    )

                    Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

                    Text(
                        textAlign = TextAlign.Center,
                        text = to.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(
                            onClick = {
                                TimePickerDialog(context,
                                    0,
                                    { _, h, m -> to.value=  to.value.withHour(h).withMinute(m)},
                                    to.value.hour, to.value.minute, true
                                ).show()
                            },
                            indication = rememberRipple(bounded = false)
                        )

                    )
                }

            }

        }

    }



    DialogButtons {

        if(modify){
            TextButton(
                onClick = {
                    display.value = false
                    GlobalScope.launch { RepositoryTrackedActivity().sessionDAO.deleteById(recordId) }
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }
        }

        TextButton(onClick = {  display.value = false }) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }



        val invalidMessage = stringResource(id = R.string.invalid_entry)

        TextButton(onClick = {
            if (valid) {
                display.value = false

                if (onSetSession == null) {
                    require(activityId != 0L)

                    val item = TrackedActivityTime(recordId, activityId, from.value, to.value)

                    GlobalScope.launch {
                        val rep = RepositoryTrackedActivity()

                        if (modify)
                            rep.sessionDAO.update(item)
                        else
                            rep.sessionDAO.insert(item)
                    }
                } else {
                    onSetSession.invoke(from.value, to.value)
                }
            } else {
                Toast.makeText(context, invalidMessage, Toast.LENGTH_LONG).show()
            }
        }) {
            Text(text = stringResource(id = if (modify) R.string.dialog_action_edit else R.string.dialog_action_add))
        }
    }

}