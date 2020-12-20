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
        where time_start >= :from AND time_start <:to
    """)
    suspend fun getAll(
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityTime>


    @Query("""
        select * from tracked_activity_session
        where time_start >= :from AND time_start <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId: Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityTime>

    @Query("""
        select TOTAL(strftime('%s',time_end) - strftime('%s', time_start)) as metric 
        from tracked_activity_session s 
        where (time_start >= :from AND time_start <:to) AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId: Long, from: LocalDateTime, to: LocalDateTime): Long


    @Query("""
        delete from tracked_activity_session
        where tracked_activity_session_id=:recordId
    """)
    suspend fun deleteById(recordId: Long)

}

