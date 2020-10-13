package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppNotificationManager
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.getitdone.database.AppDatabase
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

    fun stopSession(context: Context, item: TrackedActivity) = GlobalScope.launch {
        AppNotificationManager.removeSessionNotification(context, item.id)
        rep.commitLiveSession(item.id)
    }


    fun startSession(context: Context, item: TrackedActivity){
        AppNotificationManager.showSessionNotification(context, item)
        GlobalScope.launch {  rep.startSession(item.id) }
    }

}
