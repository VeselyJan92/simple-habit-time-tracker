package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogDatePicker
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
private fun EditableDatetime_Preview(modifier: Modifier = Modifier) = AppTheme {
    EditableDatetime(
        datetime = LocalDateTime.now(),
        onDatetimeEdit = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableDatetime(
    modifier: Modifier = Modifier,
    datetime: LocalDateTime,
    onDatetimeEdit: (LocalDateTime) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(datetime.toLocalDate())

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        val date = datePickerState.getSelectedDate()?.atTime(datetime.toLocalTime())
                            ?: LocalDateTime.now()

                        onDatetimeEdit(date)
                    }
                ) {
                    Text(text = "SET")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(datetime.hour, datetime.minute, true)

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        val date = datePickerState.getSelectedDate()
                            ?.atTime(timePickerState.hour, timePickerState.minute)
                            ?: LocalDateTime.now()

                        onDatetimeEdit(date)
                    }
                ) {
                    Text(text = "SET")
                }
            },
            title = { }
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }

    Row(
        modifier = modifier
            .height(30.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            textAlign = TextAlign.Center,
            text = datetime.format(DateTimeFormatter.ofPattern("dd. MM.")),     //TODO Local format
            modifier = Modifier.clickable(
                onClick = {
                    showDatePicker = true
                }
            )
        )

        Box(
            Modifier
                .padding(start = 8.dp, end = 8.dp)
                .size(5.dp)
                .background(Color.Black, RoundedCornerShape(50))
        )

        Text(
            textAlign = TextAlign.Center,
            text = datetime.format(DateTimeFormatter.ofPattern("HH:mm")),     //TODO Local format
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(
                onClick = {
                    showTimePicker = true
                }
            )
        )
    }
}