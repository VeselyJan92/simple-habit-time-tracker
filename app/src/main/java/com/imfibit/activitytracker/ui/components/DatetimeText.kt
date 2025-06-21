package com.imfibit.activitytracker.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DatetimeEditor(
    datetime: MutableState<LocalDateTime>
){
    val context = LocalContext.current

    Row(Modifier.padding(start = 8.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {


        Text(datetime.value.format(DateTimeFormatter.ofPattern("dd. MM.")),
            Modifier.clickable(
                onClick = {
                    DatePickerDialog(context,
                        0,
                        { _, i, i2, i3 -> datetime.value = datetime.value.withYear(i).withMonth(i2).withDayOfMonth(i3)},
                        datetime.value.year, datetime.value.month.value, datetime.value.dayOfMonth
                    ).show()
                },
            ),
        )

        Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

        Text(
            textAlign = TextAlign.Center,
            text = datetime.value.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable(
                onClick = {
                    TimePickerDialog(context,
                        0,
                        { _, h, m -> datetime.value =  datetime.value.withHour(h).withMinute(m)},
                        datetime.value.hour, datetime.value.minute, true
                    ).show()
                },
            )
        )
    }

}