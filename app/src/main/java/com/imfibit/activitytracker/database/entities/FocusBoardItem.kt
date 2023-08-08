package com.imfibit.activitytracker.database.entities

import androidx.room.*


@Entity(
    tableName = FocusBoardItem.TABLE,
    indices = [
        Index(value = ["focus_board_item_id"], name = "focus_board_item_id_pk"),
    ],
)
data class FocusBoardItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "focus_board_item_id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String = "",

    @ColumnInfo(name = "position")
    val position: Int = 0,
) {
    companion object{
        const val TABLE = "focus_board_items"
    }
}


