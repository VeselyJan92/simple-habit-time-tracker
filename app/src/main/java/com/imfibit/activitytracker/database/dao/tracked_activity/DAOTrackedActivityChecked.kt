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

    @Query("""
        select * from tracked_activity_completion
        where date_completed  >= :from AND date_completed <=:to
    """)
    suspend fun getAll(
        from: LocalDate,
        to: LocalDate
    ): List<TrackedActivityCompletion>


    @Query("""
        select * from tracked_activity_completion
        where date_completed >= :from AND date_completed <=:to AND tracked_activity_id=:activityId
    """)
    suspend fun getAll(
        activityId:Long,
        from: LocalDateTime,
        to: LocalDateTime = LocalDateTime.now()
    ): List<TrackedActivityCompletion>


    @Query("""
        select * from tracked_activity_completion
        where date_completed = :date AND tracked_activity_id =:activityId
    """)
    suspend fun getRecord(activityId: Long, date: LocalDate): TrackedActivityCompletion?


    @Query("""
        delete from tracked_activity_completion
        where tracked_activity_completion_id = :id
    """)
    suspend fun deleteById(id: Long)


    @Transaction
    suspend fun toggle(activityId: Long, date: LocalDateTime){
        val record = getRecord(activityId, date.toLocalDate())

        if (record != null)
            delete(record)
        else
            insert(TrackedActivityCompletion(0L, activityId, date.toLocalDate(), date.toLocalTime()))
    }

}