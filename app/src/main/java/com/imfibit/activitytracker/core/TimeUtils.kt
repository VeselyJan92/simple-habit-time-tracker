package com.imfibit.activitytracker.core

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.abs

object TimeUtils {
    fun secondsToMetric(seconds: Long): String {
        val h = (seconds / 3600).toInt()
        val m = (seconds - h * 3600).toInt() / 60
        val s = (seconds - h * 3600 - m * 60).toInt() / 1

        return h.toString().padStart(2, '0') + ":" + m.toString().padStart(2, '0') + ":" + s.toString().padStart(2, '0')
    }


    fun secondsToMetric(t1: LocalDateTime, t2:LocalDateTime): String {
        val seconds = abs((t1.toInstant(TimeZone.UTC) - t2.toInstant(TimeZone.UTC)).inWholeSeconds)

        return TimeUtils.secondsToMetric(seconds)
    }

    fun secondsToMetricShort(t1: LocalDateTime?, t2:LocalDateTime?): String {

        if (t1 == null || t2 == null)
            return "-"

        val seconds = abs((t1.toInstant(TimeZone.UTC) - t2.toInstant(TimeZone.UTC)).inWholeSeconds)

        val h = (seconds / 3600).toInt()
        val m = (seconds - h * 3600).toInt() / 60

        return h.toString().padStart(2, '0') + ":" + m.toString().padStart(2, '0')
    }
}
