package com.imfibit.activitytracker.database.entities

import androidx.room.*


@Entity(
    tableName = PresetTimer.TABLE,
    indices = [
        Index(value = ["preset_timer_id"], name = "preset_timer_pk"),
        Index(value = ["tracked_activity_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = TrackedActivity::class,
            parentColumns = ["tracked_activity_id"],
            childColumns = ["tracked_activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PresetTimer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preset_timer_id")
    var id: Long,

    @ColumnInfo(name = "tracked_activity_id")
    var activity_id: Long,

    @ColumnInfo(name = "seconds")
    var seconds: Int,

    @ColumnInfo(name = "position")
    var position: Int
) {
    companion object{
        const val TABLE = "preset_timer"
    }
}


