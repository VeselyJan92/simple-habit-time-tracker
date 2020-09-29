package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityCompletion: BaseEditableDAO<TrackedActivityCompletion>{

    @Query("select count(*) from tracked_activity_completion where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId")
    suspend fun getMetric(activityId:Long, from: LocalDate, to: LocalDate): Long

    suspend fun getMetric(activityId:Long, from: LocalDateTime, to: LocalDateTime) = getMetric(activityId, from.toLocalDate(), to.toLocalDate())

    @Query("""
        select * from tracked_activity_completion
        where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId:Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityCompletion>


    @Query("""
        select * from tracked_activity_completion
        where date_completed = :date AND activity_id =:activityId
    """)
    suspend fun getRecord(activityId: Long, date: LocalDate): TrackedActivityCompletion?


    @Query("""
        delete from tracked_activity_completion
        where tracked_activity_completion_id = :id
    """)
    suspend fun deleteById(id: Long)

}