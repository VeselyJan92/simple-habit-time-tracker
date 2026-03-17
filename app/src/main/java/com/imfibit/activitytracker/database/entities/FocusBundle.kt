package com.imfibit.activitytracker.database.entities

import androidx.room.*

@Entity(
    tableName = FocusBundle.TABLE,
    indices = [
        Index(value = ["focus_bundle_id"], name = "focus_bundle_id_pk"),
    ],
)
data class FocusBundle(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "focus_bundle_id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "color")
    val color: Long = 0,

    @ColumnInfo(name = "position")
    val position: Int = 0
) {
    companion object {
        const val TABLE = "focus_bundles"
    }
}
