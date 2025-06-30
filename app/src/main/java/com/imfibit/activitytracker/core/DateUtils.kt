package com.imfibit.activitytracker.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

object DateUtils {

    fun getWeeksInMonth(yearMonth: YearMonth): List<List<LocalDate>> {
        val weeks = mutableListOf<List<LocalDate>>()
        var currentDate = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        // Adjust to the start of the first week (Monday)
        currentDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        while (currentDate.isBefore(lastDayOfMonth) || currentDate.isEqual(lastDayOfMonth) || currentDate.month == yearMonth.month) {
            val week = mutableListOf<LocalDate>()
            for (i in 0..6) {
                week.add(currentDate)
                currentDate = currentDate.plusDays(1)
            }
            weeks.add(week)
            // Ensure we don't go into the next month unless the last day was part of that week
            if (currentDate.month != yearMonth.month && currentDate.isAfter(lastDayOfMonth)) {
                break
            }
        }
        return weeks
    }

}