package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import java.time.LocalDateTime


@Dao
interface DAOTrackedActivitySession : BaseEditableDAO<TrackedActivitySession>{

    @Query("""
        select * from tracked_activity_session
        where time_start >= :from AND time_start <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId: Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivitySession>

    @Query("""
        select TOTAL(strftime('%s',time_end) - strftime('%s', time_start)) as metric 
        from tracked_activity_session s 
        where (time_start >= :from AND time_start <:to) AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId: Long, from: LocalDateTime, to: LocalDateTime): Long


    @Query("""
        select * from tracked_activity_session
        where tracked_activity_id=:activityId
    """)
    suspend fun getById(activityId: Long): TrackedActivitySession

    @Query("""
        delete from tracked_activity_session
        where tracked_activity_session_id=:recordId
    """)
    suspend fun deleteById(recordId: Long)
}

