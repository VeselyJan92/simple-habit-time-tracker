package com.janvesely.activitytracker.database.embedable

import android.content.Context
import com.janvesely.activitytracker.database.composed.ViewRangeData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import java.util.*



typealias RangeInterval = List<Pair<LocalDateTime, LocalDateTime>>

enum class TimeRange {
    DAILY, WEEKLY, MONTHLY;

    fun getLabel( datetime: LocalDateTime): String = when(this){
        DAILY -> datetime.dayOfMonth.toString()
        WEEKLY -> "WEEK"
        MONTHLY ->  datetime.format(DateTimeFormatter.ofPattern("MMM"))
    }


    fun getCurrent() = getPastRanges(0).first()

    fun getPastRanges(periods: Int = 5) = when(this){
        DAILY -> getDays(periods)
        WEEKLY -> getWeeks(periods)
        MONTHLY -> getMonths(periods)
    }

    fun getDays(periods: Int):List<ViewRangeData>{
        val firstDay = LocalDateTime.now()
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            ViewRangeData(DAILY, firstDay.minusDays(it - 0L), firstDay.minusDays(it - 1L))
        }
    }

    fun getWeeks(periods: Int):List<ViewRangeData>{
        val firstDay = LocalDateTime.now()
            .with(ChronoField.DAY_OF_WEEK, 1)
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            ViewRangeData(WEEKLY, firstDay.minusDays(it*7L), firstDay.minusDays((it-1)*7L))
        }
    }

    fun getMonths(periods: Int):List<ViewRangeData>{
        val firstDay = LocalDateTime.now()
            .withDayOfMonth(1)
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            ViewRangeData(MONTHLY, firstDay.minusMonths(it+0L), firstDay.minusMonths(it-1L))
        }
    }

}


