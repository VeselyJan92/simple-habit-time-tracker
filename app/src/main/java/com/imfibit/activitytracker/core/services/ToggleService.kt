package com.imfibit.activitytracker.core.services

import android.content.Context
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject



class ToggleService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
    private val haptics: UserHapticsService
){

    suspend fun toggleActivity(activityId: Long, datetime: LocalDateTime) {
        haptics.activityFeedback()
        repository.completionDAO.toggle(activityId, datetime)
    }

}