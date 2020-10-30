package com.imfibit.activitytracker.ui.screens.statistics

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringArrayResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityWithMetric
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

class StatisticsState(
    date: LocalDate,
    range: TimeRange,
    data: Map<TrackedActivity.Type, List<ActivityWithMetric>>
) {
    var date by mutableStateOf(date)
    var range by mutableStateOf(range)
    var data by mutableStateOf(data)

    var activities by mutableStateOf(listOf<ActivityWithMetric>())

    @Composable
    fun getLabel():String = when(range){
        TimeRange.DAILY -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        TimeRange.WEEKLY -> getWeek().run { "$first - $second" }
        TimeRange.MONTHLY -> stringArrayResource(R.array.months)[date.monthValue-1]
    }



    fun getWeek(): Pair<String, String> {
        val locale = Locale.getDefault()

        val start = date.with(
            TemporalAdjusters.previousOrSame(WeekFields.of(locale).getFirstDayOfWeek())
        )

        val end = start.plusDays(6L)

        return Pair(
            start.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
            end.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        )
    }


}
