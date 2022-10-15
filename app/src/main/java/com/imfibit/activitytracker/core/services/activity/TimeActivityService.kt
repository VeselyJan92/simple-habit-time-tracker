package com.imfibit.activitytracker.core.services.activity

import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import javax.inject.Inject

class TimeActivityService @Inject constructor(
    private val db: AppDatabase
){
    val sessionDAO = db.sessionDAO

    suspend fun updateSession(record: TrackedActivityTime){
        sessionDAO.update(record)
    }

    suspend fun insertSession(session: TrackedActivityTime){
        sessionDAO.insert(session)
    }

    suspend fun deleteByRecord(recordId: Long){
        sessionDAO.deleteById(recordId)
    }

}