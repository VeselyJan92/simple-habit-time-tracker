package com.imfibit.activitytracker.ui.components.dialogs.system

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialogV2
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import java.time.LocalDate
import java.time.LocalTime


fun DialogDatePicker(date: LocalDate, onDateSet: (LocalDate)->Unit, context: Context){
    DatePickerDialog(
        context,
        0,
        { _, y, m, d -> onDateSet.invoke(LocalDate.of(y, m+1, d)) },
        date.year, date.month.value-1, date.dayOfMonth
    ).show()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date.toEpochDay())

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(LocalDate.ofEpochDay(datePickerState.selectedDateMillis ?: 0))
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(
    time: LocalTime,
    onDateSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) = BaseDialogV2(onDismissRequest = { onDismiss() }) {
    
    val datePickerState = rememberTimePickerState(time.hour, time.minute, is24Hour = true)

    DialogBaseHeader(title = stringResource(R.string.dialog_ask_for_notifications_title))

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TimePicker(
            state = datePickerState,
            colors = TimePickerDefaults.colors()
        )
    }

    DialogButtons {

        TextButton(
            onClick = {
                onDismiss()
            }
        ) {
            androidx.compose.material.Text(text = "NO NOTIFICATIONS")
        }

        TextButton(
            onClick = {
                onDismiss()
            }
        ) {
            androidx.compose.material.Text(text = "PERMIT")
        }
    }

}

