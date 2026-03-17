package com.imfibit.activitytracker.database.entities

import androidx.room.*
import com.imfibit.activitytracker.database.embedable.Markdown

@Entity(
    tableName = FocusBoardItem.TABLE,
    indices = [
        Index(value = ["focus_board_item_id"], name = "focus_board_item_id_pk"),
        Index(value = ["bundle_id"], name = "focus_board_item_bundle_id_idx"),
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
data class FocusBoardItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "focus_board_item_id")
    val id: Long = 0,

    @ColumnInfo(name = "bundle_id")
    val bundleId: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: Markdown = Markdown(""),

    @ColumnInfo(name = "bundle_position")
    val bundlePosition: Int = -1,

    @ColumnInfo(name = "dashboard_position")
    val dashboardPosition: Int? = null,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
) {
    companion object{
        const val TABLE = "focus_board_items"
    }
}