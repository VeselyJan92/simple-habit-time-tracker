package com.imfibit.activitytracker.ui.screens.statistics

import androidx.lifecycle.ViewModel
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity
) : ViewModel() {

    suspend fun getPageData(from: LocalDate, to: LocalDate, range: TimeRange): Map<TrackedActivity.Type, List<ActivityWithMetric>> {
        return rep.metricDAO.getActivitiesWithMetric(from, to)
            .filter { (it.activity.goal.range == range && it.activity.goal.isSet()) || it.metric > 0 }
            .groupBy { it.activity.type }
    }



}
