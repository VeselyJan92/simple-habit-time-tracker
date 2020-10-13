package com.janvesely.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.janvesely.activitytracker.database.entities.TrackedActivity


data class TrackedActivityGoal(
    /**Number seconds for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "goal_value")
    val value: Long,

    @ColumnInfo(name = "goal_range")
    val range: TimeRange
){
    fun isSet() = value != 0L
}