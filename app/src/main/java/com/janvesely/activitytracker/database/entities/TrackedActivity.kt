package com.janvesely.activitytracker.database.entities

import androidx.room.*
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
) {
    companion object{
        const val TABLE = "tracked_activity"
    }

    fun isGoalSet() = goal.value != 0L && type != Type.CHECKED

    enum class Type {
        SESSION, SCORE, CHECKED;

        fun format(metric: Long) = when {
            metric < 0 -> ""
            else -> {
                when(this){
                    SESSION -> String.format("%02d:%02d", metric / (60*60), (metric/60) % 60)
                    SCORE -> metric.toString()
                    CHECKED -> if (metric == 1L ) "ANO" else "NE"
                }
            }
        }

    }


    fun formatGoal() : String{
        return when(type){
            Type.SCORE -> goal.value.toString()
            Type.SESSION ->String.format("%02d:%02d", goal.value / (60*60), (goal.value/60) % 60)
            Type.CHECKED -> goal.value.toString() + "x"
        }
    }


}


