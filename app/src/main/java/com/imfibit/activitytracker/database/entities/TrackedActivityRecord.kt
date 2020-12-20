package com.imfibit.activitytracker.database.entities

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

sealed class TrackedActivityRecord(
    open var id: Long,
    open var activity_id: Long
){

    abstract val metric: Long

}

@Entity(
    tableName = TrackedActivityCompletion.TABLE,
    indices = [
        Index(value = ["tracked_activity_completion_id"]),
        Index(value = ["tracked_activity_id"]),
        Index(value = ["date_completed", "tracked_activity_id"], unique = true)
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
) : TrackedActivityRecord(id, activity_id) {

    companion object {
        const val TABLE = "tracked_activity_completion"
    }

    override val metric: Long
        get() = 1L

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
    var datetime_completed: LocalDateTime,

    @ColumnInfo(name = "score")
    var score: Long
): TrackedActivityRecord(id, activity_id){

    companion object{
        const val TABLE = "tracked_activity_score"
    }

    override val metric: Long
        get() = score
}




@Entity(
    tableName = TrackedActivityTime.TABLE,
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
data class TrackedActivityTime(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tracked_activity_session_id")
    override var id: Long,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id:  Long,

    @NonNull
    @ColumnInfo(name = "time_start")
    var datetime_start: LocalDateTime,

    @ColumnInfo(name = "time_end")
    var datetime_end: LocalDateTime
) : TrackedActivityRecord(id, activity_id) {
    companion object{
        const val TABLE = "tracked_activity_session"
    }

    override val metric: Long
        get() = Duration.between(datetime_start, datetime_end).seconds
}


