package com.janvesely.activitytracker.database.entities

import androidx.room.*
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.embedable.TrackedActivityGoal
import java.time.LocalDateTime

@Entity(
    tableName = TrackedActivity.TABLE,
    indices = [Index(value = ["tracked_activity_id"], name = "tracked_activity_pk")]
)
data class TrackedActivity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "position")
    var position: Int,

    @ColumnInfo(name = "type")
    var type: Type,

    @ColumnInfo(name = "in_session_since")
    var inSessionSince: LocalDateTime? = null,

    @Embedded
    var goal: TrackedActivityGoal,

    @ColumnInfo(name = "goal_time_range")
    var goalRange: TimeRange,

    /**Number minutes for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "goal_value")
    var goalValue: Long,
) {

    companion object{
        const val TABLE = "tracked_activity"

        val empty = TrackedActivity(0L, "", 0, Type.SESSION, inSessionSince = null, TimeRange.DAILY, 0L, "", "" )
    }

    fun isGoalSet() = goalValue != 0L && type != Type.COMPLETED

    enum class Type {
        SESSION, SCORE, COMPLETED;

        fun format(metric: Long) = when {
            metric < 0 -> ""
            else -> {
                when(this){
                    SESSION -> String.format("%02d:%02d", metric / (60*60), (metric/60) % 60)
                    SCORE -> metric.toString()
                    COMPLETED -> if (metric == 1L ) "ANO" else "NE"
                }
            }
        }

    }


    fun formatGoal() : String{
        return when(type){
            Type.SCORE -> goalValue.toString()
            Type.SESSION ->String.format("%02d:%02d", goalValue / (60*60), (goalValue/60) % 60)
            Type.COMPLETED -> goalValue.toString() + "x"
        }
    }


}


