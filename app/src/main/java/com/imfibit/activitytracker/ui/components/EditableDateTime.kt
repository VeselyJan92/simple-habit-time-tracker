package com.imfibit.activitytracker.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogDatePicker
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import androidx.compose.runtime.*

@Composable
fun EditableDatetime(
    datetime: LocalDateTime,
    onDatetimeEdit: (LocalDateTime)->Unit
){
    val context = AmbientContext.current

   // var datetime by remember {  mutableStateOf(datetime) }


    Row(
        modifier = Modifier.height(30.dp).background(Colors.ChipGray, RoundedCornerShape(50)).padding(start = 8.dp, end = 8.dp),
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
                indication = rememberRipple(bounded = false)
            )
        )

        Box(Modifier.padding(start = 8.dp, end = 8.dp).size(5.dp).background(Color.Black, RoundedCornerShape(50)))

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
                indication = rememberRipple(bounded = false)
            )

        )
    }



}