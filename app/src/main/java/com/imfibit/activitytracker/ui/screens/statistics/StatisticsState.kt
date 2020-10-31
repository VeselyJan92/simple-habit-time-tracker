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

data class StatisticsState(
    val originDate: LocalDate,
    val range: TimeRange,
    val date:LocalDate = originDate
) {

    @Composable
    fun getLabel():String = when(range){
        TimeRange.DAILY -> date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        TimeRange.WEEKLY -> range.getBoundaries(date).run { "$first - $second" }
        TimeRange.MONTHLY -> stringArrayResource(R.array.months)[date.monthValue-1]
    }

    fun offset(offset: Int) = when (range){
        TimeRange.DAILY -> originDate.minusDays(offset.toLong())
        TimeRange.WEEKLY -> originDate.minusWeeks(offset.toLong())
        TimeRange.MONTHLY -> originDate.minusMonths(offset.toLong())
    }

    fun getBoundaries(offset: Int = 0) = range.getBoundaries(offset(offset))

    fun setOffset(offset: Int) = this.copy(date = offset(offset))

    fun setRange(range: TimeRange) = this.copy(date = originDate, range = range)
    fun setDate(date: LocalDate) = this.copy(date = date)

}
