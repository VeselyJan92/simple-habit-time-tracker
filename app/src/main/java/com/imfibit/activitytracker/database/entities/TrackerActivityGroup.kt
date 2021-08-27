package com.imfibit.activitytracker.database.entities

import androidx.room.*

@Entity(
    tableName = TrackerActivityGroup.TABLE,
    indices = [
        Index(value = ["activity_group_id"], name = "activity_group_pk"),
    ]
)
data class TrackerActivityGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_group_id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "position")
    var position: Int
) {
    companion object{
        const val TABLE = "tracked_activity_group"
    }
}


