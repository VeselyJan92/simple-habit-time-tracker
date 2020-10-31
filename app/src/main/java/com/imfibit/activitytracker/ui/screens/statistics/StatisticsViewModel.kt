/*
package com.imfibit.activitytracker.ui.screens.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
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


    var data  = MutableLiveData()
    var date  = MutableLiveData(LocalDate.now())
    var range  = MutableLiveData(TimeRange.DAILY)


    val tracker = activityInvalidationTracker {
        onDataChanged()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun onDataChanged() = viewModelScope.launch(Dispatchers.IO) {
        val interval = range.value!!.getBoundaries(date.value!!)

        data.value = AppDatabase.activityRep.metricDAO.getActivitiesWithMetric(
            interval.first,
            interval.second
        ).groupBy {
            it.activity.type
        }
    }



    fun setRange(range: TimeRange) {
        this.date.value = LocalDate.now()
        this.range.value = range
        onDataChanged()
    }
}
*/
