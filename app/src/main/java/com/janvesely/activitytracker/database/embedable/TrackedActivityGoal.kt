package com.janvesely.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.janvesely.activitytracker.database.entities.TrackedActivity


data class TrackedActivityGoal(
    /**Number seconds for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "value")
    val value: Long,

    @ColumnInfo(name = "range")
    val range: TimeRange
){
    fun isSet() = value != 0L
}