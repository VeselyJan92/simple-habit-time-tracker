package com.imfibit.activitytracker.ui.screens.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity
) : ViewModel() {

    suspend fun getPageData(from: LocalDate, to: LocalDate): Map<TrackedActivity.Type, List<ActivityWithMetric>> {
        return rep.metricDAO.getActivitiesWithMetric(from, to).groupBy { it.activity.type }
    }



}
