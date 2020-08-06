package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import java.time.LocalDateTime


@Dao
interface DAOTrackedActivitySession :
    DAOTrackedActivitySessionSelectable,
    BaseEditableDAO<TrackedActivitySession>

@Dao
interface DAOTrackedActivitySessionSelectable {

    @Query("select * from tracked_activity_session where time_start >= :datetime")
    suspend fun getAllSince(datetime: LocalDateTime): List<TrackedActivitySession>

    @Query("""
        select TOTAL(strftime('%s',time_end) - strftime('%s', time_start)) as metric 
        from tracked_activity_session s 
        where time_start >= :from AND time_end <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId: Long, from: LocalDateTime, to: LocalDateTime): Int
}