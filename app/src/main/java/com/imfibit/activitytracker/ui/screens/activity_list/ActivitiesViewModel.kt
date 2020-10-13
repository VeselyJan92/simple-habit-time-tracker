package com.janvesely.activitytracker.ui.screens.activity_list

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janvesely.activitytracker.core.AppNotificationManager
import com.janvesely.activitytracker.core.activityInvalidationTracker
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.getitdone.database.AppDatabase
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
        activities.postValue(rep.getActivitiesOverview(5))
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun stopSession(activity: TrackedActivity) = GlobalScope.launch {
        rep.commitLiveSession(activity.id)
    }


    fun startSession(context: Context, item: TrackedActivity){


        GlobalScope.launch {  rep.startSession(item.id) }

        AppNotificationManager.showSessionNotification(context, item)
    }




}
