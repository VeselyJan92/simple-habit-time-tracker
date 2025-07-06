package com.imfibit.activitytracker.core.services.activity

import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import java.time.LocalDateTime
import javax.inject.Inject



class ToggleActivityService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
){

    suspend fun toggleActivity(activityId: Long, datetime: LocalDateTime) {
        repository.completionDAO.toggle(activityId, datetime)
    }

}