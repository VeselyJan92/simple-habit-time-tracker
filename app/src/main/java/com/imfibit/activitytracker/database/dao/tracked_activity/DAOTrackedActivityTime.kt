package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import java.time.LocalDateTime


@Dao
interface DAOTrackedActivityTime : BaseEditableDAO<TrackedActivityTime> {

    @Query("""
        select * from tracked_activity_session
        where datetime_start >= :from AND datetime_start <:to
    """)
    suspend fun getAll(
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityTime>


    @Query("""
        select * from tracked_activity_session
        where datetime_start >= :from AND datetime_start <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId: Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityTime>

    @Query("""
        select TOTAL(strftime('%s',datetime_end) - strftime('%s', datetime_start)) as metric 
        from tracked_activity_session s 
        where (datetime_start >= :from AND datetime_start <:to) AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId: Long, from: LocalDateTime, to: LocalDateTime): Long


    @Query("""
        delete from tracked_activity_session
        where tracked_activity_session_id=:recordId
    """)
    suspend fun deleteById(recordId: Long)


    @Query("""
        select * from tracked_activity_session
        where tracked_activity_session_id=:recordId
    """)
    suspend fun getById(recordId: Long): TrackedActivityTime

}

