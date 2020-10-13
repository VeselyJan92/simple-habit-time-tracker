package com.imfibit.activitytracker.ui.components.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.entities.TrackedActivitySession
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

    var from by  remember { mutableStateOf(if (modify) from else from.toLocalDate().atTime(15, 0)) }
    var to by remember { mutableStateOf(if (modify) to else from.plusHours(1L)) }

    val seconds = java.time.Duration.between(from , to).seconds

    val valid = from < to && seconds <= 60*60*24

    val context = ContextAmbient.current

    DialogBaseHeader(title = if (modify) "EDIT SESSION" else "ADD SESSION")

    Row(Modifier.padding(8.dp, top = 16.dp).height(50.dp), horizontalArrangement = Arrangement.SpaceBetween) {

        Column(Modifier.weight(50f).padding(end = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                textAlign = TextAlign.Center,
                text = "Start",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )

            Stack(
                modifier = Modifier
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50)).fillMaxWidth(),
                alignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = from?.format(DateTimeFormatter.ofPattern("dd. MM.")) ?: "-",
                        modifier = Modifier.clickable(
                            onClick = {

                                DatePickerDialog(context,
                                    0,
                                    { _, i, i2, i3 -> from =  from!!.withYear(i).withMonth(i2).withDayOfMonth(i3)},
                                    from!!.year, from!!.month.value, from!!.dayOfMonth
                                ).show()
                            },
                            indication = RippleIndication(bounded = false)
                        )
                    )

                    Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

                    Text(
                        textAlign = TextAlign.Center,
                        text = from.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(
                            onClick = {
                                TimePickerDialog(context,
                                    0,
                                    { _, h, m -> from =  from.withHour(h).withMinute(m)},
                                    from.hour, from.minute, true
                                ).show()
                            },
                            indication = RippleIndication(bounded = false)
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


            Stack(
                Modifier.height(30.dp)
                    .background(Colors.AppAccent, RoundedCornerShape(50))
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            TimePickerDialog(context,
                                0,
                                { _, h, m -> to = from.plusMinutes(h * 60L + m)},
                                0, 0, true
                            ).show()
                        },
                        indication = RippleIndication(bounded = false)
                    ),
                alignment = Alignment.Center,

            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = if (valid) TimeUtils.secondsToMetricShort(from, to) else "-",
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
                text = "End",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )



            Stack(
                modifier = Modifier
                    .height(30.dp).fillMaxWidth()
                    .background(if (from <= to) Colors.ChipGray else Colors.NotCompleted, RoundedCornerShape(50)),
                alignment = Alignment.Center
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = to.format(DateTimeFormatter.ofPattern("dd. MM.")),
                        modifier = Modifier.clickable(
                            onClick = {
                                DatePickerDialog(context,
                                    0,
                                    { _, i, i2, i3 -> to =  to.withYear(i).withMonth(i2).withDayOfMonth(i3)},
                                    to.year, to.month.value, to.dayOfMonth
                                ).show()
                            },
                            indication = RippleIndication(bounded = false)
                        )
                    )

                    Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

                    Text(
                        textAlign = TextAlign.Center,
                        text = to.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(
                            onClick = {
                                TimePickerDialog(context,
                                    0,
                                    { _, h, m -> to =  to.withHour(h).withMinute(m)},
                                    to.hour, to.minute, true
                                ).show()
                            },
                            indication = RippleIndication(bounded = false)
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
                Text(text = "DELETE")
            }
        }

        TextButton(onClick = {  display.value = false }) {
            Text(text = "CANCEL")
        }


        TextButton(onClick = {
            if (valid){
                display.value = false

                if (onSetSession == null){
                    require(activityId != 0L)

                    val item = TrackedActivitySession(recordId, activityId, from, to)

                    GlobalScope.launch {
                        val rep = RepositoryTrackedActivity()

                        if (modify)
                            rep.sessionDAO.update(item)
                        else
                            rep.sessionDAO.insert(item)
                    }
                }else{
                    onSetSession.invoke(from, to)
                }
            }else{
                Toast.makeText(context, "Invalid Entry", Toast.LENGTH_LONG).show()
            }
        }) {
            Text(text = if (modify) "EDIT" else "ADD")
        }
    }


}