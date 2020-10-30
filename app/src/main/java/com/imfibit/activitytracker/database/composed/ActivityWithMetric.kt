package com.imfibit.activitytracker.database.composed

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.imfibit.activitytracker.database.entities.TrackedActivity
import java.time.LocalDate

data class ActivityWithMetric(
    @Embedded
    val activity: TrackedActivity,

    @ColumnInfo(name = "metric")
    val metric: Long
)


