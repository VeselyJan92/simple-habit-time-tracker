package com.imfibit.activitytracker.ui.components.dialogs.system

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate


fun DialogDatePicker(date: LocalDate, onDateSet: (LocalDate)->Unit, context: Context){
    DatePickerDialog(
        context,
        0,
        { _, y, m, d -> onDateSet.invoke(LocalDate.of(y, m+1, d)) },
        date.year, date.month.value-1, date.dayOfMonth
    ).show()
}