package com.janvesely.activitytracker.database.dao.tracked_activity

import com.janvesely.activitytracker.database.composed.MetricAgregation

interface TrackedActivityDataDao {
    fun getMetricDaily(): MetricAgregation
    fun getMetricWeekly(): MetricAgregation
    fun getMetricMonthly(): MetricAgregation
}