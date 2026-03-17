package com.imfibit.activitytracker.database.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.*
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors


@Entity(
    tableName = FocusBoardItemTag.TABLE,
    indices = [
        Index(value = ["focus_board_item_tag_id"], name = "focus_board_item_tag_id_pk"),
        Index(value = ["bundle_id"], name = "focus_board_item_tag_bundle_id_idx"),
    ],
    foreignKeys = [
        ForeignKey(
            entity = FocusBundle::class,
            parentColumns = ["focus_bundle_id"],
            childColumns = ["bundle_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FocusBoardItemTag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "focus_board_item_tag_id")
    val id: Long = 0,

    @ColumnInfo(name = "bundle_id")
    val bundleId: Long = 0,

    @ColumnInfo(name = "title")
    val name: String = "",

    @ColumnInfo(name = "color")
    val color: Int = chooseableColors.first().toArgb(),

    @ColumnInfo(name = "position")
    val position: Int = 0,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = true,

    @ColumnInfo(name = "is_task_tag")
    val isTaskTag: Boolean = false
) {
    companion object{
        const val TABLE = "focus_board_item_tags"
    }


    fun getUIColor() =  Color(color)

}
