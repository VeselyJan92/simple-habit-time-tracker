package com.janvesely.activitytracker.database.repository.tracked_activity

import androidx.room.withTransaction
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.activitytracker.database.dao.tracked_activity.*
import com.janvesely.activitytracker.database.entities.*
import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.repository.DBEntityRepository
import java.time.LocalDateTime

class RepositoryTrackedActivity(
    override val dao: DAOTrackedActivity = AppDatabase.db.trackedActivity,
    val daoCompletion: DAOTrackedActivityCompletion = AppDatabase.db.trackedActivityCompletion,
    val daoScore: DAOTrackedActivityScore = AppDatabase.db.trackedActivityScore,
    val daoSession: DAOTrackedActivitySession = AppDatabase.db.trackedActivitySession
) : DBEntityRepository<TrackedActivity>(dao), DAOTrackedActivitySelectable by dao {


    suspend fun getAllActivities(pastRanges: Int) =  AppDatabase.db.withTransaction {
        AppDatabase.db.trackedActivity.getAll().map { activity ->

            val groupedMetric = activity.metric_range.getPastRanges(pastRanges).map {
                it.apply { metric = getMetric(activity.id, activity.type, it.from, it.to) }
            }

            TrackedActivityWithMetric(activity, groupedMetric)
        }
    }


    suspend fun getMetric(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDateTime,
        to: LocalDateTime
    ) = when(type){
        TrackedActivity.Type.SESSION -> daoSession.getMetric(activityId, from, to)
        TrackedActivity.Type.SCORE -> daoScore.getMetric(activityId, from, to)
        TrackedActivity.Type.COMPLETED -> daoCompletion.getMetric(activityId, from, to)
    }


    //fun switch(first: TrackedActivity, second: TrackedActivity) = launch { dao.switch(first, second) }


}

