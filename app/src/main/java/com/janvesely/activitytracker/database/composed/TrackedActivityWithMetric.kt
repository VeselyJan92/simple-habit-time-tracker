package com.janvesely.activitytracker.database.composed

import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.BaseMetricData
import java.sql.Time
import java.time.LocalDateTime


data class MetricBlockData(
    val type: TrackedActivity.Type,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val metric: Long,
    val label: Int,
    val color: Color,

    val activityId: Long = -1,
    val id: Long = -1,
    val range: TimeRange? = null,
) {


    fun formatMetric() = type.format(metric)

    fun isRecord() = id != -1L
}

