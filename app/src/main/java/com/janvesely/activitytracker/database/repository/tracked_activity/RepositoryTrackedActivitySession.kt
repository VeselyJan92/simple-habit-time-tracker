package com.janvesely.getitdone.database.repository

import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivitySession
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivitySessionSelectable
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import com.janvesely.activitytracker.database.repository.DBEntityRepository

class RepositoryTrackedActivitySession(
        override val dao: DAOTrackedActivitySession = AppDatabase.db.trackedActivitySession
) : DBEntityRepository<TrackedActivitySession>(dao), DAOTrackedActivitySessionSelectable by dao

