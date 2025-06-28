package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem

@Dao
abstract class DAOFocusBoardItem : BaseEditableDAO<FocusBoardItem> {

    @Query("""
        SELECT * FROM focus_board_items
        order by position
   """)
    abstract suspend fun getAll(): List<FocusBoardItem>

    @Query("""
        SELECT i.*
        FROM focus_board_items i
        left join focus_board_item_tag_relation r USING(focus_board_item_id)
        left join focus_board_item_tags t USING(focus_board_item_tag_id)
        WHERE t.is_checked = 1 or t.is_checked is NULL 
        group by i.focus_board_item_id
        order by i.position
   """)
    abstract suspend fun getAllForDashboard(): List<FocusBoardItemWithTags>

}
