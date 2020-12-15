package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityChecked: BaseEditableDAO<TrackedActivityCompletion> {


    @Query("select count(*) from tracked_activity_completion where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId")
    suspend fun getMetric(activityId:Long, from: LocalDateTime, to: LocalDateTime): Long


    /*@Query("""
        select date_completed as date, 1 as metric from tracked_activity_completion
        where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getMetricPerDay(activityId:Long, from: LocalDate, to: LocalDate): List<MetricAgreagate>*/


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


 /*   @Query("""
        select * from tracked_activity_completion
        where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getRecords(
        activityId: Long,
        from: LocalDate,
        to: LocalDate
    ):List<TrackedActivityCompletion>*/


    @Transaction
    suspend fun toggle(activityId: Long, date: LocalDate){
        val record = getRecord(activityId, date)

        if (record != null)
            delete(record)
        else
            insert(TrackedActivityCompletion(0L, activityId, date))
    }



}