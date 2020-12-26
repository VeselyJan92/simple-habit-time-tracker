package com.imfibit.activitytracker.ui.components.dialogs.system

import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalTime


fun DialogTimePicker(time: LocalTime, onTimeSet: (LocalTime)->Unit,  context: Context){
    TimePickerDialog(context,
        0,
        { _, h, m -> onTimeSet.invoke(LocalTime.of(h, m))},
        time.hour, time.minute, true
    ).show()
}