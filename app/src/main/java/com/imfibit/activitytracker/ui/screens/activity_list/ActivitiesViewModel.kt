package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.move
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val db: AppDatabase,
    private val timerService: TrackTimeService,
    private val  rep: RepositoryTrackedActivity
) : AppViewModel() {

    data class Data(
        val activities: List<TrackedActivityWithMetric> = listOf(),
        val live: List<TrackedActivity> = listOf(),
        val today: List<ActivityWithMetric> = listOf(),
        val groups: List<TrackerActivityGroup> = listOf()
    )

    val data = MutableStateFlow(Data())


    val tracker = activityInvalidationTracker {
        viewModelScope.launch(Dispatchers.IO) {
            fetch()
        }
    }

    private fun fetch() = viewModelScope.launch(Dispatchers.IO) {
        val activities = rep.activityDAO.getActivitiesWithoutCategory()

        val data =  Data(
            rep.getActivitiesOverview(activities).toMutableList(),
            rep.activityDAO.liveActive(),
            rep.metricDAO.getActivitiesWithMetric(LocalDate.now(), LocalDate.now()),
            db.groupDAO.getAll()
        )

        this@ActivitiesViewModel.data.value = data
    }

    init {
        db.invalidationTracker.addObserver(tracker)
        fetch()
    }

    override fun onCleared() {
        db.invalidationTracker.removeObserver(tracker)
    }


    fun move(from: Int, to: Int, items: List<TrackedActivityWithMetric>){
        viewModelScope.launch(Dispatchers.IO) {
            val reordered = items.mapIndexed { index, item -> item.activity.copy(position = index) }.toMutableList().apply { this.move(from, to)}


        }
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

    fun dragActivity(from: Int, to: Int){
        if (to == data.value.activities.size)
            return

        val items = data.value.activities.toMutableList().apply { move(from, to) }
        data.value = data.value.copy(activities = items)
    }

    fun moveActivity(){
        val items = data.value.activities.mapIndexed{ index, item -> item.activity.copy(position = index) }

        viewModelScope.launch(Dispatchers.IO) {
            rep.activityDAO.updateAll(*items.toTypedArray())
        }
    }


    fun addGroup(group: TrackerActivityGroup) = launchIO {
        db.groupDAO.insert(group)
    }


}
