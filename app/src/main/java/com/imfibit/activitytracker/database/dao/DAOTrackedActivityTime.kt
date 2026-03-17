package com.imfibit.activitytracker.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Dao
interface DAOTrackedActivityTime : BaseEditableDAO<TrackedActivityTime> {

    @Query("""
        select * from tracked_activity_session
        where datetime_start >= :from AND datetime_start <:to
    """)
    suspend fun getAll(
        from: LocalDateTime,
        to: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ): List<TrackedActivityTime>


    @Query("""
        select * from tracked_activity_session
        where datetime_start >= :from AND datetime_start <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId: Long,
        from: LocalDateTime,
        to: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
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
