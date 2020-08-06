package com.janvesely.activitytracker.database.entities

import androidx.room.*
import com.janvesely.activitytracker.database.embedable.TimeRange
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
    var expected: Int,


    @ColumnInfo(name = "hex_color")
    var hex_color: String,

    @ColumnInfo(name = "icon")
    var icon: String
) {
    companion object{
        const val TABLE = "tracked_activity"
    }

    fun isActivityMeasured() = expected != 0

    enum class Type {
        SESSION, SCORE, COMPLETED;

        fun format(metric: Int) =  when(this){
            SESSION -> String.format("%02d:%02d", metric / (60*60), (metric/60) % 60)
            SCORE -> metric.toString()
            COMPLETED -> if (metric == 1 ) "ANO" else "NE"
        }
    }


    fun formatGoal() : String{
        return when(type){
            Type.SCORE -> expected.toString()
            Type.SESSION ->"${expected / 60}:${expected % 60}"
            Type.COMPLETED -> ""
        }
    }


}


