package com.janvesely.activitytracker.database.repository.tracked_activity

import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.repository.DBEntityRepository

class RepositoryTrackedActivityCompletion(
        override val dao: DAOTrackedActivityCompletion = AppDatabase.db.trackedActivityCompletion
) : DBEntityRepository<TrackedActivityCompletion>(dao)

