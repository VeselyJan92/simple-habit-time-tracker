package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag

@Dao
abstract class DAOFocusBoardItemTags : BaseEditableDAO<FocusBoardItemTag> {

    @Query("SELECT * FROM ${FocusBoardItemTag.TABLE} WHERE bundle_id = :bundleId ORDER BY position")
    abstract suspend fun getAllByBundle(bundleId: Long): List<FocusBoardItemTag>

    @Query("SELECT * FROM ${FocusBoardItemTag.TABLE} ORDER BY position")
    abstract suspend fun getAll(): List<FocusBoardItemTag>
}
