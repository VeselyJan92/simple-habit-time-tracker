package com.janvesely.activitytracker.ui.components.dialogs

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.ui.components.Colors


@Composable
inline fun DialogTimeRange(
    display: MutableState<Boolean>,
    range: TimeRange,
    noinline onRangeSelected: (TimeRange)->Unit
) = BaseDialog(display = display) {

    DialogBaseHeader("Select displayed range")

    Row(Modifier.padding(8.dp)) {

        val selected = remember { mutableStateOf(range) }

        TimeRange.values().forEach {
            Stack(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .background(
                        if (selected.value == it) Colors.ChipGraySelected else Colors.ChipGray,
                        RoundedCornerShape(50)
                    )
                    .clickable(onClick = {
                        display.value = false
                        onRangeSelected.invoke(it)
                    }),

                alignment = Alignment.Center
            ) {
                Text(text = stringResource(id = it.label))
            }
        }


    }
}