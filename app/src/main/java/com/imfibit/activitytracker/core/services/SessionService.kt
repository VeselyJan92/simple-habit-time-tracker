package com.imfibit.activitytracker.core.services

import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import java.time.LocalDateTime
import javax.inject.Inject

class SessionService @Inject constructor(
      private val widgetService: TimeWidgetService,
      private val db: AppDatabase
){

    val sessionDAO = db.sessionDAO

    suspend fun updateSession(record: TrackedActivityTime){
        sessionDAO.update(record)
        widgetService.updateWidgets()
    }

    suspend fun insertSession(session: TrackedActivityTime){
        sessionDAO.insert(session)
        widgetService.updateWidgets()
    }

    suspend fun deleteByRecord(recordId: Long){
        sessionDAO.deleteById(recordId)
        widgetService.updateWidgets()
    }




}