package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Preview
@Composable
private fun MonthPreview() = TrackedActivityMonth(
    modifier = Modifier,
    activity = DevSeeder.getTrackedActivityTime(),
    month = DevSeeder.getMonthData(YearMonth.now()),
    onDayLongClicked = { _, _ -> },
    onDayClicked = { _, _ -> }
)

@Composable
fun TrackedActivityMonth(
    modifier: Modifier = Modifier,
    activity: TrackedActivity,
    month: RepositoryTrackedActivity.Month,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
) = MonthGridImpl(
    modifier,
    month = month,

    weekSum = { week ->
        val metric: ContextString = when (activity.type) {
            TrackedActivity.Type.CHECKED -> {
                { "${week.total}/7" }
            }

            else -> activity.type.getLabel(week.total)
        }
        MetricBlock(
            data = MetricWidgetData(
                value = metric,
                color = activity.goal.color(week.total),
                label = { resources.getString(R.string.week) }
            )
        )
    },
    noWeekSum = {
        Spacer(Modifier.size(40.dp, 20.dp))
    },

    day = { day ->
        val dayModifier = if (day.date == LocalDate.now())
            Modifier
                .border(width = 2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                .testTag(TestTag.MONTH_GRID_TODAY)
        else
            Modifier.testTag(TestTag.MONTH_GRID_DATE + day.date.format(DateTimeFormatter.ISO_DATE))

        MetricBlock(
            data = MetricWidgetData(activity.type.getLabel(day.metric), day.color, day.label),
            onClick = { onDayClicked(activity, day.date) },
            onLongClick = { onDayLongClicked(activity, day.date) },
            modifier = dayModifier
        )
    }

)

@Composable
fun MonthGridImpl(
    modifier: Modifier = Modifier,
    month: RepositoryTrackedActivity.Month,
    weekSum: @Composable (week: RepositoryTrackedActivity.Week) -> Unit,
    noWeekSum: @Composable () -> Unit = {
        MetricBlock(MetricWidgetData({ "-" }, Color.LightGray, { "" }))
    },
    day: @Composable (RepositoryTrackedActivity.Day) -> Unit,
) = Layout(
    modifier = modifier.padding(3.dp),
    content = {
        val now = remember { LocalDate.now() }

        MonthSplitter(month = month.month)

        month.weeks.forEach { week ->
            // Day content
            week.days.forEach { day ->
                if (day.date.month == month.month.month) {
                    day(day)
                } else {
                    Spacer(modifier = Modifier
                        .width(40.dp)
                        .height(20.dp))
                }
            }

            // Separator
            Box(modifier = Modifier
                .size(1.dp, 30.dp)
                .background(Colors.ChipGray))

            // Week sum content
            val showMetricForCurrentWeek =
                week.from.month == now.month && week.to.month != now.month

            if (week.to.month == month.month.month || showMetricForCurrentWeek) {
                weekSum(week)
            } else {
                noWeekSum()
            }
        }
    },

    ) { measurables, constraints ->
    val splitter = measurables[0].measure(constraints)

    val weekPlaceables = measurables.drop(1).chunked(9) // 7 days + separator + week sum
        .map { weekMeasurables ->
            val dayPlaceables = weekMeasurables.take(7).map { it.measure(constraints) }
            val separator = weekMeasurables[7].measure(constraints)
            val weekSumPlaceable = weekMeasurables[8].measure(constraints)
            Triple(dayPlaceables, separator, weekSumPlaceable)
        }

    val rowHeight = weekPlaceables.first().third.height
    val totalHeight = splitter.height + rowHeight * weekPlaceables.size

    layout(constraints.maxWidth, totalHeight) {
        var yPos = 0
        splitter.placeRelative(0, yPos)
        yPos += splitter.height

        weekPlaceables.forEach { (dayPlaceables, separator, weekSum) ->
            val totalItemWidth = dayPlaceables.sumOf { it.width } + separator.width + weekSum.width
            val remainingSpace = constraints.maxWidth - totalItemWidth
            val padding = remainingSpace / 8.0

            var xPos = 0.0

            dayPlaceables.forEach { day ->
                day.placeRelative(xPos.toInt(), yPos)
                xPos += (padding + day.width)
            }

            separator.placeRelative(xPos.toInt(), yPos)
            xPos += padding + separator.width

            weekSum.placeRelative(xPos.toInt(), yPos)
            yPos += rowHeight
        }
    }
}

@Composable
fun MonthSplitter(month: YearMonth) {
    val value = remember(month) {
        val name =
            month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).uppercase()

        "$name - ${month.year}"
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        HorizontalDivider(
            Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Text(text = value, fontWeight = FontWeight.W600)
        HorizontalDivider(
            Modifier
                .padding(8.dp)
                .weight(1f)
        )
    }
}