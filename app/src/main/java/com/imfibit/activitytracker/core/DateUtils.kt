package com.imfibit.activitytracker.core

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus

object DateUtils {

    fun getWeeksInMonth(year: Int, month: Int): List<List<LocalDate>> {
        val weeks = mutableListOf<List<LocalDate>>()
        var currentDate = LocalDate(year, month, 1)
        val firstDayOfMonth = currentDate
        val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)

        // Adjust to the start of the first week (Monday)
        val daysBefore = (currentDate.dayOfWeek.ordinal + 1 - (DayOfWeek.MONDAY.ordinal + 1) + 7) % 7
        currentDate = currentDate.minus(daysBefore, DateTimeUnit.DAY)

        while (currentDate < lastDayOfMonth || currentDate == lastDayOfMonth || currentDate.monthNumber == month) {
            val week = mutableListOf<LocalDate>()
            for (i in 0..6) {
                week.add(currentDate)
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }
            weeks.add(week)
            // Ensure we don't go into the next month unless the last day was part of that week
            if (currentDate.monthNumber != month && currentDate > lastDayOfMonth) {
                break
            }
        }
        return weeks
    }
}
