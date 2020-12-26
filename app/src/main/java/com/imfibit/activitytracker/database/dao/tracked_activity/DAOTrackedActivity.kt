package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.lifecycle.LiveData
import androidx.room.*
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.TrackedActivity

/////////////////////////////////
// ----- DAOTrackedTask ----- ///
/////////////////////////////////
@Dao
abstract class DAOTrackedActivity : BaseEditableDAO<TrackedActivity> {

    companion object{
        const val SQL_all_not_in_session = """
            SELECT * FROM tracked_activity 
            WHERE (type = 'TIME' AND in_session_since is NULL) OR (type != 'TIME') 
            order by position
        """
        const val SQL_one_by_id = """
            SELECT * FROM tracked_activity 
            WHERE tracked_activity_id =:id
        """
    }

    @Query(SQL_one_by_id)
    abstract suspend fun getById(id: Long): TrackedActivity

    @Query("""
            DELETE FROM tracked_activity 
            WHERE tracked_activity_id =:id
        """)
    abstract suspend fun deleteById(id: Long)

    /*@Query(SQL_one_by_id)
    abstract fun getLiveById(id: Long): LiveData<TrackedActivity>*/


    @Query("""
        SELECT * FROM tracked_activity 
        order by position
   """)
    abstract fun getAll(): List<TrackedActivity>


    @Query(SQL_all_not_in_session)
    abstract fun getAllNotInSession(): List<TrackedActivity>

   /* @Query(SQL_all_not_in_session)
    abstract fun liveAllNotInSession(): LiveData<List<TrackedActivity>>*/

    @Query("""
        SELECT * FROM tracked_activity 
        WHERE in_session_since IS NOT NULL
        order by in_session_since
    """)
    abstract fun liveActive(): LiveData<List<TrackedActivity>>




}
