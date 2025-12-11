package com.imfibit.activitytracker.database.embedable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ContextString
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

enum class TimeRange(val label: Int) {
    DAILY(R.string.frequency_daily),
    WEEKLY(R.string.frequency_weekly),
    MONTHLY(R.string.frequency_monthly);

    fun getShortLabel(date: LocalDate): ContextString = {
        when(this@TimeRange){
            DAILY -> date.dayOfMonth.toString()
            WEEKLY -> resources.getString(R.string.week)
            MONTHLY -> resources.getStringArray(R.array.months)[date.monthNumber-1]
        }
    }

    fun getDateLabel(date: LocalDate): ContextString =  {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        when(this@TimeRange){
            DAILY -> date.toJavaLocalDate().format(formatter)
            WEEKLY -> this@TimeRange.getBoundaries(date).run {
                first.toJavaLocalDate().format(formatter) + " - " + second.toJavaLocalDate().format(formatter)
            }
            MONTHLY -> resources.getStringArray(R.array.months)[date.monthNumber-1]
        }
    }


    fun getBoundaries(date: LocalDate): Pair<LocalDate, LocalDate> = when(this){
        DAILY -> Pair(date, date)
        WEEKLY -> {
            // NOTE: java.time.temporal.WeekFields is used to determine the first day of the week based on Locale.
            // kotlinx-datetime does not yet support locale-based first day of week natively.
            // For now, we will rely on java.time for this specific logic or default to Monday if acceptable, 
            // but to be safe and correct we can keep using WeekFields or write a wrapper.
            // However, since we are refactoring, we should try to minimize java.time usage.
            // But getting first day of week from Locale is a JDK feature.
            
            // value is 1 (Mon) to 7 (Sun)
            val firstDayOfWeek = java.time.temporal.WeekFields.of(Locale.getDefault()).firstDayOfWeek.value
            
            var start = date
            // ordinal is 0 (Mon) to 6 (Sun)
            // value is 1 (Mon) to 7 (Sun)
            while ((start.dayOfWeek.ordinal + 1) != firstDayOfWeek) {
                start = start.minus(1, DateTimeUnit.DAY)
            }

            val end = start.plus(6, DateTimeUnit.DAY)

            Pair(start, end)
        }
        MONTHLY -> {
             val start = LocalDate(date.year, date.monthNumber, 1)
             val end = start.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
             Pair(start, end)
        }
    }

    fun getNumberOfDays(date:LocalDate) = getBoundaries(date).run {
        first.daysUntil(second) + 1
    }

}
