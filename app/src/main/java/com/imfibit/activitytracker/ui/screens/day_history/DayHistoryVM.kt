package com.imfibit.activitytracker.ui.screens.day_history

import androidx.lifecycle.*
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.getitdone.database.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate


class DayRecordsVMFactory(val activityId: Long, val date: LocalDate) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DayRecordsVM(activityId, date) as T
    }
}

class DayRecordsVM(val activityId: Long, val date: LocalDate) : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val records = MutableLiveData<List<TrackedActivityData>>()

    val tracker = activityInvalidationTracker {
        refresh()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)
        refresh()
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun refresh() = viewModelScope.launch {

        val activity = rep.activityDAO.getById(activityId)

        val from = date.atStartOfDay()
        val to = from.plusDays(1L)

        records.postValue(rep.getRecords(activity.id, activity.type, from, to))
    }








}
