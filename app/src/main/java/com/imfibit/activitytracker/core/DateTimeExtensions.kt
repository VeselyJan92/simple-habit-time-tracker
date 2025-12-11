package com.imfibit.activitytracker.core

import android.util.Range
import androidx.core.util.rangeTo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

//from exclusive
infix fun LocalDate.iter(date: LocalDate) = DateIterator(this, date, 1)


class DateIterator(
    startDateInclusive: LocalDate,
    val endDateExclusive: LocalDate,
    val stepDays: Long
) : Iterator<LocalDate> {

    private var currentDate = startDateInclusive

    override fun hasNext() = currentDate <= endDateExclusive

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plus(stepDays.toInt(), DateTimeUnit.DAY)
        return next
    }
}


class DateIteratorReversed(
    startDate: LocalDate,
    val endDate: LocalDate, // exclusive
    val stepDays: Long
) : Iterator<LocalDate> {

    private var currentDate = startDate

    override fun hasNext() = currentDate > endDate

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.minus(stepDays.toInt(), DateTimeUnit.DAY)
        return next
    }
}


fun dateIteratorSequence(
    startDateInclusive: LocalDate,
    endDateInclusive: LocalDate,
): Sequence<LocalDate> = sequence {
    var currentDate = startDateInclusive
    while (currentDate <= endDateInclusive) {
        yield(currentDate)
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }
}


fun Range<LocalDate>.toSequence() = dateIteratorSequence(this.lower, this.upper)


fun getFullMonthBlockDays(
    year: Int,
    month: Int,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): Range<LocalDate> {
    val firstDayOfMonth = LocalDate(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)

    val daysBefore = (firstDayOfMonth.dayOfWeek.ordinal + 1 - (firstDayOfWeek.ordinal + 1) + 7) % 7
    val daysAfter = ((firstDayOfWeek.ordinal + 1) - (lastDayOfMonth.dayOfWeek.ordinal + 1) + 6) % 7

    val startDate = firstDayOfMonth.minus(daysBefore, DateTimeUnit.DAY)
    val endDate = lastDayOfMonth.plus(daysAfter, DateTimeUnit.DAY)

    return startDate rangeTo endDate
}
