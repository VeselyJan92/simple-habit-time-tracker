package com.imfibit.activitytracker.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
sealed class TrackedActivityRecord : Parcelable {
    abstract var id: Long
    abstract var activity_id: Long
    abstract val metric: Long

    @Transient
    abstract val order: LocalDateTime
}


@Parcelize
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
    override var id: Long = 0,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id: Long,

    @ColumnInfo(name = "date_completed")
    var date_completed: LocalDate,

    @ColumnInfo(name = "time_completed")
    var time_completed: LocalTime
) : TrackedActivityRecord() {

    val datetime_completed get() = time_completed.atDate(date_completed)

    @Transient
    override val order: LocalDateTime
        get() = datetime_completed

    companion object {
        const val TABLE = "tracked_activity_completion"
    }

    override val metric: Long
        get() = 1L

}

@Parcelize
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
    override var id: Long = 0,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id: Long,

    @ColumnInfo(name = "datetime_completed")
    var datetime_completed: LocalDateTime,

    @ColumnInfo(name = "score")
    var score: Long
): TrackedActivityRecord(){

    companion object{
        const val TABLE = "tracked_activity_score"

        @OptIn(ExperimentalTime::class)
        fun getEmpty(activityId: Long) = TrackedActivityScore(
            activity_id = activityId,
            datetime_completed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            score = 1
        )
    }

    @Transient
    override val order: LocalDateTime
        get() = datetime_completed

    override val metric: Long
        get() = score
}


@Parcelize
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
    override var id: Long = 0,

    @ColumnInfo(name = "tracked_activity_id")
    override var activity_id:  Long,

    @ColumnInfo(name = "datetime_start")
    var datetime_start: LocalDateTime,

    @ColumnInfo(name = "datetime_end")
    var datetime_end: LocalDateTime
) : TrackedActivityRecord() {

    companion object{
        const val TABLE = "tracked_activity_session"

        @OptIn(ExperimentalTime::class)
        fun getEmpty(activityId: Long) = TrackedActivityTime(
            activity_id = activityId,
            datetime_start = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            datetime_end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }


    @OptIn(ExperimentalTime::class)
    override val metric: Long
        get() = abs((datetime_start.toInstant(TimeZone.UTC) - datetime_end.toInstant(TimeZone.UTC)).inWholeSeconds)

    @Transient
    override val order: LocalDateTime
        get() = datetime_start
}
