package com.janvesely.activitytracker.database.composed

import java.time.LocalDate

data class MetricPerDay(val date: LocalDate, val metric: Long)