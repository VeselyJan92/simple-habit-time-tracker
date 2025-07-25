package com.imfibit.activitytracker.ui.screens.activity_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.activityTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val db: AppDatabase,
    private val timerService: TrackTimeService,
    private val  rep: RepositoryTrackedActivity,
) : BaseViewModel() {

    data class Data(
        val activities: List<TrackedActivityRecentOverview> = listOf(),
        val live: List<TrackedActivityRecentOverview> = listOf(),
        val today: List<ActivityWithMetric> = listOf(),
        val groups: List<TrackerActivityGroup> = listOf()
    )

    val data = invalidationStateFlow(db, Data(), *activityTables){
        Log.e("ActivitiesViewModel", "getActivitiesOverview")
        return@invalidationStateFlow Data(
            rep.getActivitiesOverview(rep.activityDAO.getActivitiesWithoutCategory()).toMutableList(),
            rep.getActivitiesOverview(rep.activityDAO.liveActive().filter { it.groupId != null }),
            rep.metricDAO.getActivitiesWithMetric(LocalDate.now(), LocalDate.now()).filter {
                (it.activity.goal.range == TimeRange.DAILY && it.activity.goal.isSet()) || it.metric > 0
            },
            db.groupDAO().getAll()
        )
    }

    suspend fun createNewActivity(name: String, type: TrackedActivity.Type): Long {
        val activity = TrackedActivity(
            id = 0L,
            name = name,
            position = 0,
            type = type,
            inSessionSince = null,
            goal = TrackedActivityGoal(0L, TimeRange.WEEKLY),
            challenge = TrackedActivityChallenge.empty
        )

        return rep.activityDAO.insertSync(activity)
    }

    fun onMoveActivity(from: Int, to: Int){
        data.value = data.value.copy(activities = data.value.activities.toMutableList().apply { swap(from, to) })

        launchIO {
            onDragEnd()
        }
    }

    fun onMoveGroup(from: Int, to: Int) {
        data.value = data.value.copy(groups = data.value.groups.toMutableList().apply { swap(from, to) })

        launchIO {
            onDragEnd()
        }
    }


    fun onDragEnd(){
        val activities = data.value.activities.mapIndexed{ index, item -> item.activity.copy(position = index) }

        val groups = data.value.groups.mapIndexed{ index, item -> item.copy(position = index) }

        viewModelScope.launch(Dispatchers.IO) {
            db.activityDAO().updateAll(*activities.toTypedArray())
            db.groupDAO().updateAll(*groups.toTypedArray())
        }
    }


    fun addGroup(group: TrackerActivityGroup) = launchIO {
        db.groupDAO().insert(group)
    }

}
