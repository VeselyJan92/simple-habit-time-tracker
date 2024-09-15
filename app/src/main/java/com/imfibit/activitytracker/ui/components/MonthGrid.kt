package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


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
) = Column {
    MonthSplitter(month = month.month)

    Column(modifier = modifier.padding(3.dp)) {
        month.weeks.forEach {
            MonthGridWeek(
                week = it,
                month = month.month.month,
                weekSum = {
                    val metric: ContextString = when (activity.type) {
                        TrackedActivity.Type.CHECKED -> {
                            { "${it.total}/7" }
                        }

                        else -> activity.type.getLabel(it.total)
                    }

                    MetricBlock(
                        data = MetricWidgetData(
                            value = metric,
                            color = activity.goal.color(it.total),
                            label = { resources.getString(R.string.week) }
                        )
                    )
                },

                day = {
                    val dayModifier = if (it.date == LocalDate.now())
                        Modifier
                            .border(width = 2.dp, Color.Black, shape = RoundedCornerShape(50))
                            .testTag(TestTag.MONTH_GRID_TODAY)
                    else
                        Modifier.testTag(TestTag.MONTH_GRID_DATE + it.date.format(DateTimeFormatter.ISO_DATE))

                    MetricBlock(
                        data = MetricWidgetData(activity.type.getLabel(it.metric), it.color, it.label),
                        onClick = {
                            onDayClicked(activity, it.date)
                        },
                        onLongClick = { onDayLongClicked(activity, it.date) },
                        modifier = dayModifier
                    )
                }
            )
        }
    }
}

@Composable
fun MonthGridWeek(
    week: RepositoryTrackedActivity.Week,
    month: Month,
    weekSum: @Composable (week: RepositoryTrackedActivity.Week) -> Unit,
    noWeekSum: @Composable () -> Unit = {
        MetricBlock(MetricWidgetData({ "-" }, Color.LightGray, { "" }))
    },
    day: @Composable (RepositoryTrackedActivity.Day) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.days.forEach {
            if (it.date.month == month) {
                day(it)
            } else {
                Spacer(modifier = Modifier.width(40.dp).height(20.dp))
            }
        }


        Box(modifier = Modifier.size(1.dp, 30.dp).background(Colors.ChipGray))

        val currentMonth = LocalDate.now().month
        val showMetricForCurrentWeek = week.from.month == currentMonth && week.to.month != currentMonth

        if (week.to.month == month || showMetricForCurrentWeek) {
            weekSum(week)
        } else {
            noWeekSum()
        }
    }

}

@Composable
fun MonthSplitter(month: YearMonth) {
    val value = remember(month) {
        val name = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).uppercase()

        "$name - ${month.year}"
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Divider(
            Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Text(text = value, fontWeight = FontWeight.W600)
        Divider(
            Modifier
                .padding(8.dp)
                .weight(1f)
        )
    }
}