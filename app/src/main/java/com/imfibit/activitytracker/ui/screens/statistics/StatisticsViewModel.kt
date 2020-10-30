package com.imfibit.activitytracker.ui.screens.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.getitdone.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class StatisticsViewModel() : ViewModel() {

    val rep = RepositoryTrackedActivity()

    var date by mutableStateOf(LocalDate.now())
    var range by mutableStateOf(TimeRange.DAILY)
    var data by mutableStateOf(mapOf<TrackedActivity.Type, List<ActivityWithMetric>>())


    val tracker = activityInvalidationTracker {
        onDataChanged()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)

        onDataChanged()
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun onDataChanged() = viewModelScope.launch(Dispatchers.IO) {
        val interval = range.getBoundaries(date)
        data = AppDatabase.activityRep.metricDAO.getActivitiesWithMetric(
            interval.first,
            interval.second
        ).groupBy {
            it.activity.type
        }
    }

    fun onSwipedCard(offset: Int) {
        val now = LocalDate.now()
        val offset = offset.toLong()
        date = when (range){
            TimeRange.DAILY -> now.minusDays(offset)
            TimeRange.WEEKLY -> now.minusWeeks(offset)
            TimeRange.MONTHLY -> now.minusMonths(offset)
        }
    }
}
