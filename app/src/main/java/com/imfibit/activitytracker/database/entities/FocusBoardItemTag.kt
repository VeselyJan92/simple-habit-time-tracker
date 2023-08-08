package com.imfibit.activitytracker.database.entities

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.*


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
    var color: Int = colors.first().toArgb(),

    @ColumnInfo(name = "position")
    var position: Int = 0
) {
    companion object{
        const val TABLE = "focus_board_item_tags"

        val colors = listOf(
            Color(0xFFE1BEE7),
            Color(0xFFD1C4E9),
            Color(0xFFC5CAE9),
            Color(0xFFBBDEFB),
            Color(0xFFB3E5FC),
            Color(0xFFB2DFDB),
            Color(0xFFC8E6C9),
            Color(0xFFDCEDC8),
            Color(0xFFF0F4C3),
            Color(0xFFFFF9C4),
            Color(0xFFFFECB3),
        )
    }


    fun getUIColor() =  Color(color)

}





