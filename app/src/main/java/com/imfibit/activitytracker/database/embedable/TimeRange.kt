package com.imfibit.activitytracker.database.embedable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*


typealias RangeInterval = List<Pair<LocalDateTime, LocalDateTime>>


data class Range(val from: LocalDateTime, val to: LocalDateTime)

enum class TimeRange(val label: Int) {
    DAILY(R.string.frequency_daily),
    WEEKLY(R.string.frequency_weekly),
    MONTHLY(R.string.frequency_monthly);

    @Composable
    fun getLabel( datetime: LocalDate): String = when(this){
        DAILY -> datetime.dayOfMonth.toString()
        WEEKLY -> stringResource(id = R.string.week)
        MONTHLY -> datetime.format(DateTimeFormatter.ofPattern("MMM"))
    }

    fun getBoundaries(date: LocalDate) = when(this){
        DAILY -> Pair(date, date)
        WEEKLY -> {
            val start = date.with(
                TemporalAdjusters
                    .previousOrSame(WeekFields.of(Locale.getDefault())
                    .getFirstDayOfWeek())
            )

            val end = start.plusDays(6L)

            Pair(start, end)
        }
        MONTHLY -> Pair(date.withDayOfMonth(0), date.withDayOfMonth(date.lengthOfMonth()))
    }




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


