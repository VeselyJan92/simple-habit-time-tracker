package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.janvesely.activitytracker.database.composed.MetricAgregation
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityScore : BaseEditableDAO<TrackedActivityScore>, TrackedActivityDataDao{

    @Query("""
        SELECT
           DATE(, 'weekday 0') AS this_sunday,
           COUNT(*) AS rows_this_week
        FROM tracked_activity_score
        GROUP BY this_sunday;
    """)
    override fun getMetricWeekly(): List<MetricAgregation>


    @Query("""
        select date(time_completed) as date, TOTAL(score) as metric 
        from tracked_activity_score s 
        where time_completed >= :from AND time_completed <:to AND tracked_activity_id=:activityId
        GROUP BY  date
    """)
    suspend fun getMetricPerDay(activityId:Long, from: LocalDate, to: LocalDate): List<MetricAgregation>



    @Query("""
        select TOTAL(score) + 0 as metric 
        from tracked_activity_score s 
        where time_completed >= :from AND time_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId:Long, from: LocalDateTime, to: LocalDateTime): Long




    @Query("""
        select * from tracked_activity_score
        where time_completed >= :from AND time_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId:Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityScore>


    @Query("""
        delete from tracked_activity_score
        where tracked_activity_score_id=:recordId
    """)
    suspend fun deleteById(recordId: Long)


    suspend fun commitScore(activityId: Long, datetime: LocalDateTime,  score: Long){
        insert(TrackedActivityScore(0, activityId, datetime, score))
    }
}

