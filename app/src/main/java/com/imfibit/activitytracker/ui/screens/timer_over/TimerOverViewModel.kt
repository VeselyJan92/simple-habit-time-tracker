package com.imfibit.activitytracker.ui.screens.timer_over

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TimerOverViewModel : ViewModel() {

    val rep = RepositoryTrackedActivity()


    val live = rep.activityDAO.liveActive()

    val tracker = activityInvalidationTracker{
        refresh()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)

        refresh()
    }

    fun refresh() = viewModelScope.launch {
        val activities =rep.getActivitiesOverview(5)
        Log.e("VM", "activities: $activities")
       // this@TimerOverViewModel.activities.postValue(activities.toMutableList())
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun stopSession(context: Context, item: TrackedActivity) = GlobalScope.launch {
        NotificationLiveSession.remove(context, item.id)
        rep.commitLiveSession(item.id)
    }




}
