package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val db: AppDatabase,
    private val timerService: TrackTimeService,
    private val  rep: RepositoryTrackedActivity
) : AppViewModel() {

    data class Data(
        val activities: List<TrackedActivityRecentOverview> = listOf(),
        val live: List<TrackedActivityRecentOverview> = listOf(),
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
            rep.getActivitiesOverview(rep.activityDAO.liveActive().filter { it.groupId != null }),
            rep.metricDAO.getActivitiesWithMetric(LocalDate.now(), LocalDate.now()).filter {
                (it.activity.goal.range == TimeRange.DAILY && it.activity.goal.isSet()) || it.metric > 0
            },
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

    suspend fun addActivity(activity: TrackedActivity): Long {
        return rep.activityDAO.insertSync(activity)

    }

    fun onMoveActivity(from: Int, to: Int){
        data.value = data.value.copy(activities = data.value.activities.toMutableList().apply { swap(from, to) })
    }

    fun onMoveGroup(from: Int, to: Int) {
        data.value = data.value.copy(groups = data.value.groups.toMutableList().apply { swap(from, to) })
    }


    fun onDragEnd(){
        val activities = data.value.activities.mapIndexed{ index, item -> item.activity.copy(position = index) }

        val groups = data.value.groups.mapIndexed{ index, item -> item.copy(position = index) }

        viewModelScope.launch(Dispatchers.IO) {
            db.activityDAO.updateAll(*activities.toTypedArray())
            db.groupDAO.updateAll(*groups.toTypedArray())
        }
    }


    fun addGroup(group: TrackerActivityGroup) = launchIO {
        db.groupDAO.insert(group)
    }

    fun clearOnboardingData(context: Context) = launchIO  {
        db.clearAllTables()

        context.dataStore.edit {
            it[PreferencesKeys.ERASE_OBOARDING_SHOW] = false
        }

    }

    fun hideClearCard(context: Context) = launchIO {
        context.dataStore.edit {
            it[PreferencesKeys.ERASE_OBOARDING_SHOW] = false
        }
    }



}
