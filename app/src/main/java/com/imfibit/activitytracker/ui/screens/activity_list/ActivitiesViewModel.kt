package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.burnoutcrew.reorderable.move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val db: AppDatabase,
    private val timerService: TrackTimeService,
    private val  rep: RepositoryTrackedActivity
) : ViewModel() {

    val activities = MutableLiveData<MutableList<TrackedActivityWithMetric>>()

    val live = rep.activityDAO.liveActive()

    val tracker = activityInvalidationTracker{
        refresh()
    }




    init {
        db.invalidationTracker.addObserver(tracker)

        refresh()
    }

    fun refresh() = viewModelScope.launch {
        val activities =rep.getActivitiesOverview(5)
        this@ActivitiesViewModel.activities.postValue(activities.toMutableList())
    }

    override fun onCleared() {
        db.invalidationTracker.removeObserver(tracker)
    }


    fun activityTriggered(activity: TrackedActivity) = viewModelScope.launch {
        when (activity.type) {
            TrackedActivity.Type.TIME -> startSession(activity)
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

    fun startSession(activity: TrackedActivity) {
        viewModelScope.launch(Dispatchers.IO) { timerService.startSession(activity) }
    }

    fun commitSession(activity: TrackedActivity) {
        viewModelScope.launch(Dispatchers.IO) { timerService.commitSession(activity) }
    }

    fun updateSession(activity: TrackedActivity, start: LocalDateTime) = viewModelScope.launch {
        timerService.updateSession(activity, start)
    }

    suspend fun addActivity(activity: TrackedActivity): Long {
        return rep.activityDAO.insertSync(activity)

    }


}
