package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.entities.FocusBundle

@Dao
interface DAOFocusBundle : BaseEditableDAO<FocusBundle> {

    @Query("SELECT * FROM ${FocusBundle.TABLE} ORDER BY position")
    suspend fun getAll(): List<FocusBundle>

    @Query("SELECT * FROM ${FocusBundle.TABLE} WHERE focus_bundle_id = :id")
    suspend fun getById(id: Long): FocusBundle?

    @Update
    override suspend fun updateAll(vararg entity: FocusBundle)

}
