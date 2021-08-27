package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.*
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup

/////////////////////////////////
// ----- DAOPresetTimers ----- ///
/////////////////////////////////
@Dao
abstract class DAOActivityGroup : BaseEditableDAO<TrackerActivityGroup> {

    @Query("""
        SELECT * FROM tracked_activity_group
        order by position 
   """)
    abstract fun getAll(): List<TrackerActivityGroup>

    @Query("""
        SELECT * FROM tracked_activity_group
        where activity_group_id = :groupId
   """)
    abstract fun getById(groupId: Long): TrackerActivityGroup



}
