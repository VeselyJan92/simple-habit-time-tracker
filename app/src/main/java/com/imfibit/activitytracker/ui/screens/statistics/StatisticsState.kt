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

    var origin by mutableStateOf(default)
    var range by mutableStateOf(range)
    var date by mutableStateOf(date)


    @OptIn(ExperimentalPagerApi::class)
    var pager  = PagerState(
        pageCount = 100,
        currentPage = 51
    )


    fun offset(offset: Int) = when (range){
        TimeRange.DAILY -> origin.minusDays(offset.toLong())
        TimeRange.WEEKLY -> origin.minusWeeks(offset.toLong())
        TimeRange.MONTHLY -> origin.minusMonths(offset.toLong())
    }

    @OptIn(ExperimentalPagerApi::class)
    fun setTimeRange(range: TimeRange){
        this.origin = default
        this.date = default
        this.range = range

        pager  = PagerState(
            pageCount = 100,
            currentPage = 51
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    fun setCustomDate(date: LocalDate){
        origin = date
        this.date = date
        pager  = PagerState(
            pageCount = 100,
            currentPage = 51
        )
    }

    fun getRange(page: Int) = range.getBoundaries(offset(originPage - page))

}
