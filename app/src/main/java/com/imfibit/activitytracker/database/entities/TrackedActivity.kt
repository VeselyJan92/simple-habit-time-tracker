package com.imfibit.activitytracker.database.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.imfibit.activitytracker.R
import androidx.room.*
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = TrackedActivity.TABLE,
    indices = [
        Index(value = ["tracked_activity_id"], name = "tracked_activity_pk"),
        Index(value = ["activity_group_id"], name = "tracked_activity_group_fk")
  ],
    foreignKeys = [
        ForeignKey(
            entity = TrackerActivityGroup::class,
            parentColumns = ["activity_group_id"],
            childColumns = ["activity_group_id"]
        )
    ]
)
data class TrackedActivity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_id")
    var id: Long,

    @ColumnInfo(name = "activity_group_id")
    var groupId: Long? = null,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "position")
    var position: Int = 0,

    @ColumnInfo(name = "group_position")
    var groupPosition: Int = 0,

    @ColumnInfo(name = "type")
    var type: Type,

    @ColumnInfo(name = "in_session_since")
    var inSessionSince: LocalDateTime? = null,

    @Embedded
    var goal: TrackedActivityGoal,

    @ColumnInfo(name = "timer")
    val timer: Int? = null,
) {
    companion object{
        const val TABLE = "tracked_activity"
    }

    fun isGoalSet() = goal.value != 0L

    enum class Type {
        TIME,
        SCORE,
        CHECKED;
        
        private fun formatSession(metric: Long) = String.format("%02d:%02d", metric / (60*60), (metric/60) % 60)

        fun getComposeString(
            metric: Long,
            fraction:Long? = null
        ): ComposeString = {
            when(this){
                TIME ->  when {
                    fraction != null -> "${formatSession(metric)} / $fraction"
                    else -> formatSession(metric)
                }
                SCORE ->when {
                    fraction != null -> "$metric / $fraction"
                    else -> metric.toString()
                }

                CHECKED -> when {
                    fraction != null -> "$metric / $fraction"
                    metric == 0L -> stringResource(id = R.string.no).toUpperCase()
                    metric == 1L  -> stringResource(id = R.string.yes).toUpperCase()
                    else -> metric.toString()
                }
            }
        }

        @Composable
        fun getMetricString(metric: Long, fraction:Long? = null) = getComposeString(metric, fraction)()

        fun getCheckedFraction(range: TimeRange, from: LocalDate): Long?{
            require(this == CHECKED)

            return when(range){
                TimeRange.DAILY -> null
                TimeRange.WEEKLY -> 7
                TimeRange.MONTHLY -> from.lengthOfMonth().toLong()
            }
        }

    }

    fun isInSession() = inSessionSince != null






    fun formatGoal() : String{
        return when(type){
            Type.SCORE -> goal.value.toString()
            Type.TIME ->String.format("%02d:%02d", goal.value / (60*60), (goal.value/60) % 60)
            Type.CHECKED -> goal.value.toString() + "x"
        }
    }


}


