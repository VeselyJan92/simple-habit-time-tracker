package com.imfibit.activitytracker.database.dao

import android.util.Log
import androidx.room.*
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DAOFocusBoardItemTags : BaseEditableDAO<FocusBoardItemTag> {

    @Query("""
        SELECT * FROM focus_board_item_tags
        order by position
   """)
    abstract fun getAllFlow(): Flow<List<FocusBoardItemTag>>


    @Query("""
        SELECT t.* FROM focus_board_item_tags t
        join focus_board_item_tag_relation r USING(focus_board_item_tag_id)
        join focus_board_items i USING(focus_board_item_id)
        WHERE focus_board_item_id = :focusItemId
        order by i.focus_board_item_id, r.primary_tag DESC
   """)
    abstract fun getFocusItemTag(focusItemId: Long): List<FocusBoardItemTag>




}
