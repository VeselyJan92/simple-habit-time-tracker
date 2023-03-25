package com.imfibit.activitytracker.database.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

@Entity(
    tableName = TrackedActivity.TABLE,
    indices = [
        Index(value = ["tracked_activity_id"], name = "tracked_activity_pk"),
        Index(value = ["activity_group_id"], name = "tracked_activity_group_fk")
  ],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
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

    @Embedded
    var challenge: TrackedActivityChallenge,

    @ColumnInfo(name = "timer")
    var timer: Int? = null,
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

        fun getLabel(
            metric: Long,
            fraction:Long? = null
        ): ContextString = {
            when(this@Type){
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
                    metric == 0L -> resources.getString(R.string.no).uppercase()
                    metric == 1L  -> resources.getString(R.string.yes).uppercase()
                    else -> metric.toString()
                }
            }
        }

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

    fun getChallengeRemainingDays(metric: Long): Int {
        return  ((challenge.target - metric) / goal.metricPerDay()).toInt()
    }
    fun getChallengeEstimatedCompletionDate(metric: Long): LocalDate {
        return LocalDate.now().plusDays(getChallengeRemainingDays(metric).toLong())
    }

    fun getChallengeAheadDays(metric: Long): Int {
        return Period.between(getChallengeEstimatedCompletionDate(metric), challenge.to).days
    }

    fun formatGoal() : String{
        return when(type){
            Type.SCORE -> goal.value.toString()
            Type.TIME ->String.format("%02d:%02d", goal.value / (60*60), (goal.value/60) % 60)
            Type.CHECKED -> goal.value.toString() + "x"
        }
    }


}


