package com.janvesely.activitytracker.ui.screens.activity_list

import android.util.Log
import androidx.lifecycle.*
import com.janvesely.activitytracker.core.activityInvalidationTracker
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivitiesViewModel : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val activities = MutableLiveData<List<TrackedActivityWithMetric>>()

    val live = rep.activityDAO.liveActive()

    val tracker = activityInvalidationTracker{
        refresh()
    }


    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)

        refresh()
    }

    fun refresh() = viewModelScope.launch {
        activities.postValue(rep.getAllActivities(5))
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun stopSession(activity: TrackedActivity) = GlobalScope.launch {
        rep.commitLiveSession(activity.id)
    }



}
