package com.imfibit.activitytracker.database.dao

import android.util.Log
import androidx.room.*
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBoardItemTagRelation
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DAOFocusBoardItemTagRelation : BaseEditableDAO<FocusBoardItemTagRelation> {


    @Query("DELETE FROM focus_board_item_tag_relation WHERE focus_board_item_id = :focusItemId")
    abstract suspend fun deleteTagsFromFocusItem(focusItemId: Long)

    @Transaction
    public open suspend fun updateTags(item: FocusBoardItemWithTags){
        deleteTagsFromFocusItem(item.item.id)

        item.tags.forEachIndexed { index, it ->

            Log.e("tag", "Save tag relation: " + it.toString() + ", primary: " + (index == 0))

            insert(FocusBoardItemTagRelation(item.item.id, it.id, index == 0))
        }
    }

}
