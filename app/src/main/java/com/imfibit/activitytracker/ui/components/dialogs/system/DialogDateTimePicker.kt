package com.imfibit.activitytracker.ui.components.dialogs.system

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Preview
@Composable
private fun DialogTimePicker_Preview() = AppTheme {
    DialogTimePicker(
        onDismissRequest = {},
        onTimePicked = { _, _ -> }
    )
}


@Preview
@Composable
private fun DatePickerDialog_Preview() = AppTheme {
    DatePickerDialog(
        onDismissRequest = {},
        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        onDatePicked = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogTimePicker(
    onDismissRequest: () -> Unit,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimePicked: (hour: Int, minute: Int) -> Unit,
) {
    val timePickerState = rememberTimePickerState(initialHour, initialMinute, true)

    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onTimePicked(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text(text = stringResource(id = R.string.action_set))
            }
        },
        title = { }
    ) {
        TimePicker(
            state = timePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    date: LocalDate,
    onDatePicked: (LocalDate?) -> Unit,
) {
    val datePickerState = rememberDatePickerState(date.toJavaLocalDate())

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onDatePicked(datePickerState.getSelectedDate()?.toKotlinLocalDate())
                }
            ) {
                Text(text = stringResource(id = R.string.action_set))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}
