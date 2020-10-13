package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.ui.components.Colors


@Composable
inline fun DialogTimeRange(
    display: MutableState<Boolean>,
    range: TimeRange,
    noinline onRangeSelected: (TimeRange)->Unit
) = BaseDialog(display = display) {

    DialogBaseHeader("Select displayed range")

    Row(Modifier.padding(8.dp)) {

        val selected = remember { mutableStateOf(range) }

        val values = TimeRange.values()

        values.forEachIndexed{ index, timeRange ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (values.size - 1 == index) 0.dp else 8.dp)
                    .height(30.dp)
                    .background(
                        if (selected.value == timeRange) Colors.ChipGraySelected else Colors.ChipGray,
                        RoundedCornerShape(50)
                    )
                    .clickable(onClick = {
                        display.value = false
                        onRangeSelected.invoke(timeRange)
                    }),

                alignment = Alignment.Center
            ) {
                Text(text = stringResource(id = timeRange.label))
            }


        }


    }
}