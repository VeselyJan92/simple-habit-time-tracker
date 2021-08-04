package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppNotificationManager
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import io.burnoutcrew.reorderable.move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ActivitiesViewModel : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val activities = MutableLiveData<MutableList<TrackedActivityWithMetric>>()

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
        this@ActivitiesViewModel.activities.postValue(activities.toMutableList())
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


    fun activityTriggered(activity: TrackedActivity, context: Context) = GlobalScope.launch{
        when (activity.type) {
            TrackedActivity.Type.TIME -> startSession(context, activity)
            TrackedActivity.Type.SCORE -> rep.scoreDAO.commitScore(activity.id, LocalDateTime.now(), 1)
            TrackedActivity.Type.CHECKED -> rep.completionDAO.toggle(activity.id, LocalDateTime.now())
        }
    }

    fun move(from: Int, to: Int, items: List<TrackedActivityWithMetric>){
        viewModelScope.launch(Dispatchers.IO) {
            val reordered = items.mapIndexed { index, item -> item.activity.copy(position = index) }.toMutableList().apply { move(from, to)}

            rep.activityDAO.updateAll(*reordered.toTypedArray())

        }
    }

}
