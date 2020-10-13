package com.janvesely.activitytracker.database.embedable

import androidx.compose.runtime.Composable
import com.janvesely.activitytracker.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField


typealias RangeInterval = List<Pair<LocalDateTime, LocalDateTime>>


data class Range(val from: LocalDateTime, val to: LocalDateTime)

enum class TimeRange(val label: Int) {
    DAILY(R.string.frequency_daily),
    WEEKLY(R.string.frequency_weekly),
    MONTHLY(R.string.frequency_monthly);

    @Composable
    fun getLabel( datetime: LocalDate): String = when(this){
        DAILY -> datetime.dayOfMonth.toString()
        WEEKLY -> "W" + datetime.dayOfMonth.toString()
        MONTHLY ->  datetime.format(DateTimeFormatter.ofPattern("MMM"))
    }


    fun getCurrent() = getPastRanges(0).first()

    fun getPastRanges(periods: Int = 5) = when(this){
        DAILY -> getDays(periods)
        WEEKLY -> getWeeks(periods)
        MONTHLY -> getMonths(periods)
    }

    fun getDays(periods: Int):List<Range>{
        val firstDay = LocalDateTime.now()
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            Range(firstDay.minusDays(it - 0L), firstDay.minusDays(it - 1L))
        }
    }

    fun getWeeks(periods: Int):List<Range>{
        val firstDay = LocalDateTime.now()
            .with(ChronoField.DAY_OF_WEEK, 1)
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            Range(firstDay.minusDays(it*7L), firstDay.minusDays((it-1)*7L))
        }
    }

    fun getMonths(periods: Int):List<Range>{
        val firstDay = LocalDateTime.now()
            .withDayOfMonth(1)
            .toLocalDate().atStartOfDay()

        return (0 until periods).map {
            Range(firstDay.minusMonths(it+0L), firstDay.minusMonths(it-1L))
        }
    }

}


