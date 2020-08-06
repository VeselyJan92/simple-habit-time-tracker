package com.janvesely.activitytracker.database.dao.tracked_activity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.getitdone.database.dao.BaseEditableDAO
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface DAOTrackedActivityCompletion: BaseEditableDAO<TrackedActivityCompletion>{

    @Query("select count(*) from tracked_activity_completion where date_completed >= :from AND date_completed <:to AND tracked_activity_id=:activityId")
    suspend fun getMetric(activityId:Long, from: LocalDate, to: LocalDate): Int

    suspend fun getMetric(activityId:Long, from: LocalDateTime, to: LocalDateTime) = getMetric(activityId, from.toLocalDate(), to.toLocalDate())

    @Query("select * from tracked_activity_completion")
    suspend fun getAll(): List<TrackedActivityCompletion>

}