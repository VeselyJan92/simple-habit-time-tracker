package com.imfibit.activitytracker.ui.screens.statistics

import android.util.Log
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.res.stringArrayResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.PagerState
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityWithMetric
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*

class StatisticsState(
    val default: LocalDate,
    range: TimeRange,
    clock: AnimationClockObservable,
    date:LocalDate = default
) {
    val minPage = 0
    val maxPage = 101
    val originPage = 51

    var origin by mutableStateOf(default)
    var range by mutableStateOf(range)
    var date by mutableStateOf(date)

    val pager = PagerState(clock, originPage, minPage = minPage, maxPage = maxPage){
        this@StatisticsState.date = offset(originPage - this.currentPage)
    }


    fun offset(offset: Int) = when (range){
        TimeRange.DAILY -> origin.minusDays(offset.toLong())
        TimeRange.WEEKLY -> origin.minusWeeks(offset.toLong())
        TimeRange.MONTHLY -> origin.minusMonths(offset.toLong())
    }

    fun setTimeRange(range: TimeRange){
        this.origin = default
        this.date = default
        this.range = range
        pager.currentPage = originPage
    }

    fun setCustomDate(date: LocalDate){
        origin = date
        this.date = date
        pager.currentPage = originPage
    }

    fun getRange(page: Int) = range.getBoundaries(offset(originPage - page))

}
