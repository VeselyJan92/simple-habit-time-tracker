package com.imfibit.activitytracker.ui.components.dialogs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.EditableDatetime
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import com.imfibit.activitytracker.ui.components.layout.LabeledColumn
import java.time.LocalDateTime
import java.time.LocalTime




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogSession(
    display: MutableState<Boolean> = mutableStateOf(true),
    record: TrackedActivityTime,
    onUpdate: ((LocalDateTime, LocalDateTime)->Unit),
    onDelete: (()->Unit)? = null
) = DialogSession(display, record.id != 0L,record.datetime_start, record.datetime_end, onUpdate, onDelete )


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogSession(
    display: MutableState<Boolean> = mutableStateOf(true),
    allowDelete: Boolean,
    from: LocalDateTime,
    to: LocalDateTime,
    onUpdate: ((LocalDateTime, LocalDateTime)->Unit),
    onDelete: (()->Unit)? = null
)  = BaseDialog(display = display) {

    val from = remember { mutableStateOf(from) }
    val to = remember { mutableStateOf(to) }

    val seconds = java.time.Duration.between(from.value , to.value).seconds

    val valid = from.value < to.value && seconds <= 60*60*24

    val context = LocalContext.current

    DialogBaseHeader(title = stringResource(id = if (allowDelete) R.string.dialog_session_title_edit else R.string.dialog_session_title_add))

    Row(
        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        LabeledColumn(text = stringResource(id = R.string.session_start)) {
            EditableDatetime(
                datetime = from.value,
                onDatetimeEdit = {
                    from.value =  it
                }
            )
        }

        LabeledColumn(text = "") {
            val interaction = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier.height(30.dp).width(60.dp)
                    .background(Colors.AppAccent, RoundedCornerShape(50))
                    .clickable(
                        interactionSource = interaction,
                        onClick = {
                            DialogTimePicker(
                                time = LocalTime.of(0 , 0),
                                onTimeSet = {to.value = from.value.plusMinutes(it.hour * 60L + it.minute)},
                                context = context
                            )
                        },
                        indication = rememberRipple(bounded = false)
                    ).padding(horizontal = 8.dp),
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


        LabeledColumn(text = stringResource(id = R.string.session_end)) {
            EditableDatetime(
                datetime = to.value,
                onDatetimeEdit = {
                    to.value =  it
                }
            )
        }

    }



    DialogButtons {
        if(allowDelete){
            TextButton(
                onClick = {
                    display.value = false
                    onDelete!!.invoke()
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
                onUpdate.invoke(from.value, to.value)
            } else {
                Toast.makeText(context, invalidMessage, Toast.LENGTH_LONG).show()
            }
        }) {
            Text(text = stringResource(id = if (allowDelete) R.string.dialog_action_edit else R.string.dialog_action_add))
        }
    }

}