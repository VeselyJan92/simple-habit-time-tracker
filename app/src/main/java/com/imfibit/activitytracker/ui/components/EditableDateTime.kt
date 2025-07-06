package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.dialogs.system.DatePickerDialog
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
private fun EditableDatetime_Preview() = AppTheme {
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
    var showTimePicker by remember { mutableStateOf(false) }
    if (showTimePicker) {
        DialogTimePicker(
            onDismissRequest = { showTimePicker = false },
            initialHour = datetime.hour,
            initialMinute = datetime.minute,
            onTimePicked = { hour, minute ->
                val date = datetime.withHour(hour).withMinute(minute) ?: LocalDateTime.now()
                onDatetimeEdit(date)
            }
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            date = datetime.toLocalDate(),
            onDatePicked = {
                val date = it?.atTime(datetime.hour, datetime.minute) ?: LocalDateTime.now()
                onDatetimeEdit(date)
            }
        )
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