package com.janvesely.activitytracker.database.entities

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TrackedActivityData(
    open var id: Long,
    open var activity_id: Long
)

@Entity(
    tableName = TrackedActivityCompletion.TABLE,
    indices = [
        Index(value = ["tracked_activity_completion_id"]),
        Index(value = ["tracked_activity_id"]),
        Index(value = ["date_completed"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = TrackedActivity::class,
            parentColumns = ["tracked_activity_id"],
            childColumns = ["tracked_activity_id"],
            onDelete = CASCADE
        )
    ]
)
data class TrackedActivityCompletion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_completion_id")
    override var id: Long,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id: Long,

    @NonNull
    @ColumnInfo(name = "date_completed")
    var date_completed: LocalDate
) : TrackedActivityData(id, activity_id) {

    companion object {
        const val TABLE = "tracked_activity_completion"
    }

}

@Entity(
    tableName = TrackedActivityScore.TABLE,
    indices = [
        Index(value = ["tracked_activity_score_id"]),
        Index(value = ["tracked_activity_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TrackedActivity::class,
            parentColumns = ["tracked_activity_id"],
            childColumns = ["tracked_activity_id"],
            onDelete = CASCADE
        )
    ]
)
data class TrackedActivityScore(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_score_id")
    override var id: Long,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id: Long,

    @ColumnInfo(name = "time_completed")
    var time_completed: LocalDateTime,

    @ColumnInfo(name = "score")
    var score: Int
): TrackedActivityData(id, activity_id){

    companion object{
        const val TABLE = "tracked_activity_score"
    }
}




@Entity(
    tableName = TrackedActivitySession.TABLE,
    indices = [
        Index(value = ["tracked_activity_session_id"]),
        Index(value = ["tracked_activity_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TrackedActivity::class,
            parentColumns = ["tracked_activity_id"],
            childColumns = ["tracked_activity_id"],
            onDelete = CASCADE
        )
    ]
)
data class TrackedActivitySession(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_session_id")
    override var id: Long,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id:  Long,

    @NonNull
    @ColumnInfo(name = "time_start")
    var time_start: LocalDateTime,

    @ColumnInfo(name = "time_end")
    var time_end: LocalDateTime
) : TrackedActivityData(id, activity_id) {
    companion object{
        const val TABLE = "tracked_activity_session"
    }


    fun getTimeInSeconds() = Duration.between(time_start, time_end).seconds
}


