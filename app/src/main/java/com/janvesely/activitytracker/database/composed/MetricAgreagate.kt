package com.janvesely.activitytracker.database.composed

import java.time.LocalDate

data class MetricAgregation(val from: LocalDate, val to: LocalDate, val metric: Long)