package com.imfibit.activitytracker.ui.screens.group

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ActivityGroupViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity,
    private val db: AppDatabase,
    private val savedStateHandle: SavedStateHandle
) : AppViewModel() {

    val id: Long = savedStateHandle["group_id"] ?: throw IllegalArgumentException()


    //For better edittext performance save the name of the activity when user is done with the screen
    val groupName = mutableStateOf<String?>(null)


    val activities = MutableStateFlow<List<TrackedActivityRecentOverview>>(listOf())

    val group = MutableStateFlow<TrackerActivityGroup?>(null)

    val groups = MutableStateFlow<List<TrackerActivityGroup>>(listOf())


    val tracker = activityInvalidationTracker {
        fetch()
    }

    private fun fetch() = viewModelScope.launch(Dispatchers.IO) {
        //there might be and update after delete group
        val groupData = db.groupDAO.getByIdOrNull(id) ?: return@launch



        activities.value = rep.getActivitiesOverview(db.activityDAO.getActivitiesFromGroup(id))
        groups.value = db.groupDAO.getAll()

        group.value = groupData
        groupName.value = groupData.name
    }

    init {
        db.invalidationTracker.addObserver(tracker)
        fetch()
    }

    override fun onCleared()  = runBlocking(Dispatchers.IO) {
        db.invalidationTracker.removeObserver(tracker)

        val group = db.groupDAO.getByIdOrNull(id)

        // If name is not filled or group was deleted
        if (!groupName.value.isNullOrBlank() && group != null)
            db.groupDAO.update(group.copy(name = groupName.value!!))

    }

    fun refreshName(name: String){
        this.groupName.value = name
    }

    fun moveGroup(from: Int, to: Int) {
        this.groups.value = this.groups.value.toMutableList().apply { swap(from, to) }
    }

    fun onGroupDragEnd(from: Int, to: Int) = viewModelScope.launch {
        val items = this@ActivityGroupViewModel.groups.value
            .mapIndexed{index, item -> item.copy(position = index)}
            .toTypedArray()

        db.groupDAO.updateAll(*items)
    }

    fun moveActivity(from: Int, to: Int) {
        this.activities.value = this.activities.value.toMutableList().apply { swap(from, to) }
    }

    fun onActivityDragEnd(from: Int, to: Int) = viewModelScope.launch(Dispatchers.IO){
        val items = this@ActivityGroupViewModel.activities.value
            .mapIndexed{index, item -> item.activity.copy(groupPosition = index)}
            .toTypedArray()

        db.activityDAO.updateAll(*items)
    }

    fun delete(item: TrackerActivityGroup) = launchIO {
        db.groupDAO.delete(item)
    }

}
