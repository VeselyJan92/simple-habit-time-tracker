package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.entities.*

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun Record(
    activity: TrackedActivity,
    record: TrackedActivityRecord,
    onCLick: (TrackedActivityRecord) -> Unit = {},
) {
    Surface(
        shadowElevation = 2.dp,
        modifier = Modifier
            .testTag(TestTag.ACTIVITY_RECORD)
            .clickable(onClick = {
                onCLick(record)
            }),
        shape = RoundedCornerShape(20.dp),
        color = Colors.SuperLight
    ) {
        val time = with(AnnotatedString.Builder()) {
            append(stringResource(id = R.string.time) + ": ")

            when (record) {
                is TrackedActivityCompletion -> {
                    append(
                        record.datetime_completed.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                }

                is TrackedActivityScore -> {
                    append(
                        record.datetime_completed.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                }

                is TrackedActivityTime -> {
                    append(
                        record.datetime_start.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                    append(" - ")
                    append(record.datetime_end.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                }
            }

            toAnnotatedString()
        }

        val metric = activity.type.getLabel(record.metric).value()


        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = activity.name,
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp
                    )
                )

                Text(
                    text = time,
                    modifier = Modifier.padding(start = 16.dp),
                    style = TextStyle.Default.copy(color = Color.Black.copy(alpha = 0.6f)),
                )
            }


            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            ) {
                MetricBlock(metric = metric)
            }

        }


    }

}

@Composable
private fun MetricBlock(metric: String) {
    Box(
        modifier = Modifier
            .size(60.dp, 25.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = metric,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        )
    }
}
