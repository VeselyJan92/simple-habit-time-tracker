package com.imfibit.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.database.entities.TrackedActivity
import java.time.LocalDate


data class TrackedActivityGoal(
    /**Number seconds for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "goal_value")
    val value: Long,

    @ColumnInfo(name = "goal_range")
    val range: TimeRange
){
    fun isSet() = value != 0L

}