package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityScore:
        DAOTrackedTaskScoreSelectable,
    BaseEditableDAO<TrackedActivityScore>

@Dao
interface DAOTrackedTaskScoreSelectable {


    @Query("""
        select TOTAL(score) + 0 as metric 
        from tracked_activity_score s 
        where time_completed >= :from AND time_completed <:to AND tracked_activity_id=:activityId
    """)
    suspend fun getMetric(activityId:Long, from: LocalDateTime, to: LocalDateTime): Int
}