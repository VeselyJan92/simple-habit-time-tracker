package com.janvesely.activitytracker.database.entities

import android.graphics.Color
import androidx.room.*
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.ui.components.Colors
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

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

    @ColumnInfo(name = "in_session")
    var in_session_since: LocalDateTime? = null,

    @ColumnInfo(name = "metric_range")
    var metric_range: TimeRange,

    /**Number minutes for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "expected")
    var expected: Long,


    @ColumnInfo(name = "hex_color")
    var hex_color: String,

    @ColumnInfo(name = "icon")
    var icon: String
) {
    companion object{
        const val TABLE = "tracked_activity"

        val empty = TrackedActivity(0L, "", 0, Type.SESSION, in_session_since = null, TimeRange.DAILY, 0L, "", "" )

    }

    fun isGoalSet() = expected != 0L && type != Type.COMPLETED

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
            Type.SCORE -> expected.toString()
            Type.SESSION ->String.format("%02d:%02d", expected / (60*60), (expected/60) % 60)
            Type.COMPLETED -> ""
        }
    }


}


