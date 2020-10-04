package com.janvesely.activitytracker.core

import java.time.LocalDate


infix fun LocalDate.iter(date: LocalDate) = if (this < date)
    DateIterator(this, date.minusDays(1L), 1)
else
    DateIteratorReversed(this, date, 1)


class DateIterator(
    startDate: LocalDate,
    val endDateInclusive: LocalDate,
    val stepDays: Long
) : Iterator<LocalDate> {

    private var currentDate = startDate

    override fun hasNext() = currentDate <= endDateInclusive

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plusDays(stepDays)
        return next
    }
}


class DateIteratorReversed(
    startDate: LocalDate,
    val endDateInclusive: LocalDate,
    val stepDays: Long
) : Iterator<LocalDate> {

    private var currentDate = startDate

    override fun hasNext() = currentDate >= endDateInclusive

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.minusDays(stepDays)
        return next
    }
}

