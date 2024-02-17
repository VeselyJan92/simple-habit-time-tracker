package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogTimePicker
import com.imfibit.activitytracker.ui.components.dialogs.system.MyDatePickerDialog
import com.imfibit.activitytracker.ui.components.dialogs.system.MyTimePickerDialog
import java.time.LocalDate
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
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .padding(start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val source1 = remember { MutableInteractionSource()}


        var dateDialog by rememberDialog();

        if (dateDialog){
            MyDatePickerDialog(LocalDate.now(), onDateSelected = {}, onDismiss = { dateDialog = false})
        }

        Text(
            textAlign = TextAlign.Center,
            text = datetime.format(DateTimeFormatter.ofPattern("dd. MM.")),     //TODO Local format
            modifier = Modifier.clickable(
                onClick = {
                    dateDialog = true
                },
                indication = rememberRipple(bounded = false),
                interactionSource = source1
            )
        )

        Box(
            Modifier
                .padding(start = 8.dp, end = 8.dp)
                .size(5.dp)
                .background(Color.Black, RoundedCornerShape(50)))

        val source2 = remember { MutableInteractionSource()}

        var timeDialog by rememberDialog();

        if (timeDialog){
            MyTimePickerDialog(
                time = datetime.toLocalTime(),
                onDateSelected = {timeDialog = false},
                onDismiss = { timeDialog = false }
            )
        }

        Text(
            textAlign = TextAlign.Center,
            text = datetime.format(DateTimeFormatter.ofPattern("HH:mm")),     //TODO Local format
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.clickable(
                onClick = {
                    timeDialog = true
                },
                indication = rememberRipple(bounded = false),
                interactionSource = source2
            )

        )
    }



}