package com.imfibit.activitytracker.core

import android.util.Range
import androidx.core.util.rangeTo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

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
        currentDate = currentDate.plusDays(stepDays)
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
        currentDate = currentDate.minusDays(stepDays)
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
        currentDate = currentDate.plusDays(1)
    }
}


fun Range<LocalDate>.toSequence() = dateIteratorSequence(this.lower, this.upper)


fun getFullMonthBlockDays(
    year: Int,
    month: Int,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): Range<LocalDate> {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

    val daysBefore = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val daysAfter = (firstDayOfWeek.value - lastDayOfMonth.dayOfWeek.value + 6) % 7

    val startDate = firstDayOfMonth.minusDays(daysBefore.toLong())
    val endDate = lastDayOfMonth.plusDays(daysAfter.toLong())

    return startDate rangeTo endDate
}


