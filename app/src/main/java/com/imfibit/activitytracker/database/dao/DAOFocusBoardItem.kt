package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DAOFocusBoardItem : BaseEditableDAO<FocusBoardItem> {

    @Query("""
        SELECT * FROM focus_board_items
        order by position
   """)
    abstract fun getAll(): List<FocusBoardItem>

}
