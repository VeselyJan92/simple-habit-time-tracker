package com.imfibit.activitytracker.ui.screens.statistics

import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.imfibit.activitytracker.database.embedable.TimeRange
import java.time.LocalDate
import java.util.*

class StatisticsState(
    val default: LocalDate,
    range: TimeRange,
    date:LocalDate = default,
) {
    val minPage = 0
    val maxPage = 101
    val originPage = 51

    var origin = mutableStateOf(default)
    var range = mutableStateOf(range)
    var date = mutableStateOf(date)


    @OptIn(ExperimentalPagerApi::class)
    var pager  = PagerState(
        pageCount = 100,
        currentPage = 51
    )


    fun offset(offset: Int) = when (range.value){
        TimeRange.DAILY -> origin.value.minusDays(offset.toLong())
        TimeRange.WEEKLY -> origin.value.minusWeeks(offset.toLong())
        TimeRange.MONTHLY -> origin.value.minusMonths(offset.toLong())
    }

    @OptIn(ExperimentalPagerApi::class)
    fun setTimeRange(range: TimeRange){
        this.origin.value = default
        this.date.value = default
        this.range.value = range

        pager  = PagerState(
            pageCount = 100,
            currentPage = 51
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    fun setCustomDate(date: LocalDate){
        origin.value = date
        this.date.value = date
        pager  = PagerState(
            pageCount = 100,
            currentPage = 51
        )
    }

    fun getRange(page: Int) = range.value.getBoundaries(offset(originPage - page))

}
