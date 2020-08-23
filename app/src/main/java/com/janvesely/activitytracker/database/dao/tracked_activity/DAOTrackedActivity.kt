package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.lifecycle.LiveData
import androidx.room.*
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import com.janvesely.getitdone.database.AppDatabase
import java.time.LocalDateTime

/////////////////////////////////
// ----- DAOTrackedTask ----- ///
/////////////////////////////////
@Dao
abstract class DAOTrackedActivity :
    DAOTrackedActivitySelectable,
    BaseEditableDAO<TrackedActivity>{

}


interface DAOTrackedActivitySelectable {
    @Query("""
        SELECT * FROM tracked_activity 
        order by position
   """)
    fun getAllx(): List<TrackedActivity>


    @Query("""
        SELECT * FROM tracked_activity 
        WHERE (type = 'SESSION' AND in_session is NULL) OR (type != 'SESSION') 
        order by position
   """)
    fun getAllWithoutInSession(): List<TrackedActivity>


    @Query("""
        SELECT * FROM tracked_activity 
        WHERE in_session IS NOT NULL
        order by in_session
    """)
    fun liveActive(): LiveData<List<TrackedActivity>>


}