package com.imfibit.activitytracker.database.entities

import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TrackedActivityRecord{
    abstract var id: Long
    abstract var activity_id: Long
    abstract val metric: Long

    abstract val order: LocalDateTime
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

    @ColumnInfo(name = "date_completed")
    var date_completed: LocalDate,

    @ColumnInfo(name = "time_completed")
    var time_completed: LocalTime
) : TrackedActivityRecord() {

    val datetime_completed get() = time_completed.atDate(date_completed)

    @Transient
    override val order = datetime_completed

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

    @ColumnInfo(name = "datetime_completed")
    var datetime_completed: LocalDateTime,

    @ColumnInfo(name = "score")
    var score: Long
): TrackedActivityRecord(){

    companion object{
        const val TABLE = "tracked_activity_score"
    }

    @Transient
    override val order = datetime_completed

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
    @ColumnInfo(name = "datetime_start")
    var datetime_start: LocalDateTime,

    @ColumnInfo(name = "datetime_end")
    var datetime_end: LocalDateTime
) : TrackedActivityRecord() {
    companion object{
        const val TABLE = "tracked_activity_session"
    }

    override val metric: Long
        get() = Duration.between(datetime_start, datetime_end).seconds

    @Transient
    override val order = datetime_start
}


