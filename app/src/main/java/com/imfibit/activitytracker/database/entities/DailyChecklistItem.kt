package com.imfibit.activitytracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(
    tableName = DailyChecklistItem.TABLE,
    indices = [
        Index(value = ["daily_checklist_item_id"], name = "daily_checklist_item_id_pk"),
    ],
)
data class DailyChecklistItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "daily_checklist_item_id")
    val id: Long = 0,

    @ColumnInfo(name = "color")
    val color: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "date_checked")
    val date_checked: LocalDate? = null,

    @ColumnInfo(name = "position")
    val position: Int = 0,
) {
    companion object {
        const val TABLE = "daily_checklist_items"
    }
}
