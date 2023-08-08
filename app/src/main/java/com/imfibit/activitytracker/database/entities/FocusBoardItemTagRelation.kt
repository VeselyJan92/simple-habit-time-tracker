package com.imfibit.activitytracker.database.entities

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.*


@Entity(
    tableName = FocusBoardItemTagRelation.TABLE,
    indices = [
        Index(value = ["focus_board_item_id"], name = "focus_board_item_id_fk"),
        Index(value = ["focus_board_item_tag_id"], name = "focus_board_item_tag_fk"),
        Index(value = ["focus_board_item_tag_id", "focus_board_item_id"], name = "focus_board_item_tag_relation_pk", unique = true),
    ],

    primaryKeys = ["focus_board_item_id", "focus_board_item_tag_id"],

    foreignKeys =[
        ForeignKey(
            entity = FocusBoardItem::class,
            parentColumns = ["focus_board_item_id"],
            childColumns = ["focus_board_item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FocusBoardItemTag::class,
            parentColumns = ["focus_board_item_tag_id"],
            childColumns = ["focus_board_item_tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FocusBoardItemTagRelation(
    @ColumnInfo(name = "focus_board_item_id")
    val focusItemId: Long,

    @ColumnInfo(name = "focus_board_item_tag_id")
    val focusItemTagId: Long,

    @ColumnInfo(name = "primary_tag")
    val primaryTag: Boolean
) {
    companion object{
        const val TABLE = "focus_board_item_tag_relation"
    }
}





