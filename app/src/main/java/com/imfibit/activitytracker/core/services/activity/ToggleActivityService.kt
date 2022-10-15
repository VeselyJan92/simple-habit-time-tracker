package com.imfibit.activitytracker.core.services.activity

import com.imfibit.activitytracker.core.services.UserHapticsService
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import java.time.LocalDateTime
import javax.inject.Inject



class ToggleActivityService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
    private val haptics: UserHapticsService
){

    suspend fun toggleActivity(activityId: Long, datetime: LocalDateTime) {
        haptics.activityFeedback()
        repository.completionDAO.toggle(activityId, datetime)
    }

}