package com.janvesely.getitdone.database.repository

import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivityScore
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedTaskScoreSelectable
import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.repository.DBEntityRepository

class RepositoryTrackedActivityScore(
        override val dao: DAOTrackedActivityScore = AppDatabase.db.trackedActivityScore
) : DBEntityRepository<TrackedActivityScore>(dao), DAOTrackedTaskScoreSelectable by dao

