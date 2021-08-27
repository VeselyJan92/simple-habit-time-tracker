package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.*
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.TrackedActivity
import kotlinx.coroutines.flow.Flow

/////////////////////////////////
// ----- DAOTrackedTask ----- ///
/////////////////////////////////
@Dao
abstract class DAOTrackedActivity : BaseEditableDAO<TrackedActivity> {


    @Query("""
        SELECT * FROM tracked_activity 
        WHERE tracked_activity_id =:id
    """)
    abstract fun flowById(id: Long): Flow<TrackedActivity>

    @Query("""
        SELECT * FROM tracked_activity 
        WHERE tracked_activity_id =:id
    """)
    abstract fun getById(id: Long): TrackedActivity

    @Query("""
        DELETE FROM tracked_activity 
        WHERE tracked_activity_id =:id
    """)
    abstract suspend fun deleteById(id: Long)


    @Query("""
        SELECT * FROM tracked_activity 
        order by position
    """)
    abstract fun getAll(): List<TrackedActivity>


    @Query("""
        SELECT * FROM tracked_activity
        WHERE activity_group_id = :groupId
        order by group_position 
   """)
    abstract fun getActivitiesFromGroup(groupId: Long): List<TrackedActivity>

    @Query("""
        SELECT * FROM tracked_activity
        WHERE activity_group_id is NULL
        order by position 
   """)
    abstract fun getActivitiesWithoutCategory(): List<TrackedActivity>


    @Query(""" 
        SELECT * FROM tracked_activity 
        WHERE (type = 'TIME' AND in_session_since is NULL) OR (type != 'TIME') 
        order by position
    """)
    abstract fun getAllNotInSession(): List<TrackedActivity>


    @Query("""
        SELECT * FROM tracked_activity 
        WHERE in_session_since IS NOT NULL
        order by in_session_since
    """)
    abstract fun liveActive(): List<TrackedActivity>

    @Transaction
    override suspend fun updateAll(vararg entity: TrackedActivity) {
        entity.forEach {
            update(it)
        }
    }

}
