package com.janvesely.activitytracker.database.composed

import androidx.room.ColumnInfo
import java.time.LocalDate

data class MetricAggregation(
    @ColumnInfo(name = "from_date")
    val from: LocalDate,

    @ColumnInfo(name = "to_date")
    val to: LocalDate,
    val metric: Long
)


fun List<MetricAggregation>.toHashMap(): HashMap<LocalDate, MetricAggregation> {
    val map = HashMap<LocalDate, MetricAggregation>()
    this.forEach {  map[it.from] = it}
    return map
}



data class MetricAgreagate(val date: LocalDate, val metric: Long)