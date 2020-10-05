package com.janvesely.activitytracker.core

import java.time.LocalDate

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

