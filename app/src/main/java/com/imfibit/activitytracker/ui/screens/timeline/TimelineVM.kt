package com.imfibit.activitytracker.ui.screens.timeline

import androidx.lifecycle.*
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import kotlinx.coroutines.launch
import java.time.LocalDate

class TimelineVM : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val records = MutableLiveData<List<RecordWithActivity>>(listOf())

    private val tracker = activityInvalidationTracker {
        refresh()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)
        refresh()
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    private fun refresh() = viewModelScope.launch {

        val records = rep.getAllRecordsWithActivity(
            LocalDate.now().atStartOfDay(),
            LocalDate.now().atStartOfDay().plusDays(1)
        )

        this@TimelineVM.records.postValue(records)
    }

}
