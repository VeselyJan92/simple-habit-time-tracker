package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem

@Dao
abstract class DAOFocusBoardItem : BaseEditableDAO<FocusBoardItem> {

    @Query("""
        SELECT * FROM focus_board_items
        WHERE bundle_id = :bundleId
        ORDER BY bundle_position
   """)
    abstract suspend fun getAllByBundle(bundleId: Long): List<FocusBoardItem>

    @Transaction
    @Query("""
        SELECT i.*
        FROM focus_board_items i
        LEFT JOIN focus_board_item_tag_relation r USING(focus_board_item_id)
        LEFT JOIN focus_board_item_tags t USING(focus_board_item_tag_id)
        WHERE i.bundle_id = :bundleId AND (t.is_checked = 1 OR t.is_checked IS NULL)
        GROUP BY i.focus_board_item_id
        ORDER BY i.bundle_position
   """)
    abstract suspend fun getItemsWithTagsByBundle(bundleId: Long): List<FocusBoardItemWithTags>

    @Transaction
    @Query("""
        SELECT *
        FROM focus_board_items
        WHERE is_starred = 1
        ORDER BY dashboard_position
   """)
    abstract suspend fun getDashboardItems(): List<FocusBoardItemWithTags>

    @Transaction
    @Query("""
        SELECT *
        FROM focus_board_items
        WHERE focus_board_item_id = :noteId
   """)
    abstract suspend fun getItemWithTags(noteId: Long): FocusBoardItemWithTags?

    @Query("""
        UPDATE focus_board_items
        SET is_completed = :isCompleted, completed_at = :completedAt
        WHERE focus_board_item_id = :itemId
    """)
    abstract suspend fun updateCompletionStatus(itemId: Long, isCompleted: Boolean, completedAt: Long?)

}