package com.imfibit.activitytracker.database.embedable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ContextString
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*


enum class TimeRange(val label: Int) {
    DAILY(R.string.frequency_daily),
    WEEKLY(R.string.frequency_weekly),
    MONTHLY(R.string.frequency_monthly);

    fun getShortLabel(date: LocalDate): ContextString = {
        when(this@TimeRange){
            DAILY -> date.dayOfMonth.toString()
            WEEKLY -> resources.getString(R.string.week)
            MONTHLY -> resources.getStringArray(R.array.months)[date.monthValue-1]
        }
    }

    fun getDateLabel(date: LocalDate): ContextString =  {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

        when(this@TimeRange){
            DAILY -> date.format(formatter)
            WEEKLY -> this@TimeRange.getBoundaries(date).run {
                first.format(formatter) + " - " + second.format(formatter)
            }
            MONTHLY -> resources.getStringArray(R.array.months)[date.monthValue-1]
        }
    }


    fun getBoundaries(date: LocalDate) = when(this){
        DAILY -> Pair(date, date)
        WEEKLY -> {
            val start = date.with(
                TemporalAdjusters
                    .previousOrSame(WeekFields.of(Locale.getDefault()).firstDayOfWeek)
            )

            val end = start.plusDays(6L)

            Pair(start, end)
        }
        MONTHLY -> Pair(date.withDayOfMonth(1), date.withDayOfMonth(date.lengthOfMonth()))
    }

    fun getNumberOfDays(date:LocalDate) = getBoundaries(date).run {
        Period.between(first, second).days + 1
    }


  /*  fun getPastRanges(periods: Int = 5) = when(this){
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
    }*/

}


