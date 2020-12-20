package com.imfibit.activitytracker.core

import com.imfibit.activitytracker.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.Month

object TimeUtils {

    inline fun secondsToMetric(t1: LocalDateTime, t2:LocalDateTime): String {

        val seconds = Duration.between(t1, t2).seconds

        val h = (seconds / 3600).toInt()
        val m = (seconds - h * 3600).toInt() / 60
        val s = (seconds - h * 3600 - m * 60).toInt() / 1

        return (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
    }

    inline fun secondsToMetricShort(t1: LocalDateTime?, t2:LocalDateTime?): String {

        if (t1 == null || t2 == null)
            return "-"

        val seconds = Duration.between(t1, t2).seconds

        val h = (seconds / 3600).toInt()
        val m = (seconds - h * 3600).toInt() / 60

        return (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m)
    }




}