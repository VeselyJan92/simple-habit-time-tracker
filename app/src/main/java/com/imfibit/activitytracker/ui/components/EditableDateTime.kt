package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogDatePicker
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditableDatetime(
    modifier: Modifier = Modifier,
    datetime: LocalDateTime,
    onDatetimeEdit: (LocalDateTime)->Unit
){
    val context = LocalContext.current


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
                    DialogDatePicker(
                        date = datetime.toLocalDate(),
                        onDateSet = {
                            onDatetimeEdit.invoke(datetime.toLocalTime().atDate(it))
                        },
                        context = context
                    )
                },
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
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable(
                onClick = {
                    DialogTimePicker(
                        time = datetime.toLocalTime(),
                        onTimeSet = {
                            onDatetimeEdit.invoke(datetime.toLocalDate().atTime(it))
                        },
                        context = context
                    )
                },
            )

        )
    }



}