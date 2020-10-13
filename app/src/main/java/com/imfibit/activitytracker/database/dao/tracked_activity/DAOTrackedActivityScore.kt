package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.janvesely.activitytracker.database.composed.MetricAggregation
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityScore : BaseEditableDAO<TrackedActivityScore>, DAOTrackedActivityMetric{



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

