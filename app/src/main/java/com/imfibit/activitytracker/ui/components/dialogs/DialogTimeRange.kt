package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors

@Preview
@Composable
fun DialogTimeRange_Preview() = AppTheme {
    DialogTimeRange(
        onDismissRequest = {},
        range = TimeRange.WEEKLY,
        onRangeSelected = {}
    )
}

@Composable
fun DialogTimeRange(
    onDismissRequest: () -> Unit,
    range: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
) = BaseDialog(
    onDismissRequest = onDismissRequest
) {
    DialogBaseHeader(stringResource(id = R.string.dialog_time_range_title))

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val selected = remember { mutableStateOf(range) }

        val values = TimeRange.entries

        values.forEachIndexed { index, timeRange ->
            Text(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected.value == timeRange) Colors.ChipGraySelected else Colors.ChipGray,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .clickable(
                        onClick = {
                            onDismissRequest()
                            onRangeSelected.invoke(timeRange)
                        }
                    ),
                textAlign = TextAlign.Center,
                text = stringResource(id = timeRange.label))
        }

    }

    DialogButtons {
        TextButton(
            onClick = onDismissRequest
        ) {
            Text(text = "OK")
        }
    }
}