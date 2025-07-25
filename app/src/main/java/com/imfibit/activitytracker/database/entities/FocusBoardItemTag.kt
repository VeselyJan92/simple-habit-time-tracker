package com.imfibit.activitytracker.database.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.*
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors


@Entity(
    tableName = FocusBoardItemTag.TABLE,
    indices = [
        Index(value = ["focus_board_item_tag_id"], name = "focus_board_item_tag_id_pk"),
    ],
)
data class FocusBoardItemTag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "focus_board_item_tag_id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val name: String = "",

    @ColumnInfo(name = "color")
    val color: Int = chooseableColors.first().toArgb(),

    @ColumnInfo(name = "position")
    val position: Int = 0,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = true
) {
    companion object{
        const val TABLE = "focus_board_item_tags"
    }


    fun getUIColor() =  Color(color)

}





