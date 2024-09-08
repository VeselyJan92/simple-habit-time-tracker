package com.imfibit.activitytracker.ui.screens.group

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.activityTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.invalidationStateFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ActivityGroupViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity,
    private val db: AppDatabase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val id = savedStateHandle.toRoute<Destinations.ScreenActivityGroupRoute>().groupId


    //For better edittext performance save the name of the activity when user is done with the screen
    val groupName = mutableStateOf<String?>(null)


    val activities = invalidationStateFlow(db, listOf(), *activityTables){
        rep.getActivitiesOverview(db.activityDAO().getActivitiesFromGroup(id))
    }

    val group = invalidationStateFlow(db, null, *activityTables){
        val group = db.groupDAO().getByIdOrNull(id)

        if (group != null){
            viewModelScope.launch(Dispatchers.Main) {
                groupName.value = group.name
            }
        }

        group
    }


    override fun onCleared()  = runBlocking(Dispatchers.IO) {
        val group = db.groupDAO().getByIdOrNull(id)

        // If name is not filled or group was deleted
        if (!groupName.value.isNullOrBlank() && group != null)
            db.groupDAO().update(group.copy(name = groupName.value!!))

    }

    fun refreshName(name: String){
        this.groupName.value = name
    }


    fun moveActivity(from: Int, to: Int) {
        this.activities.value = this.activities.value.toMutableList().apply { swap(from, to) }
    }

    fun onActivityDragEnd(from: Int, to: Int) = viewModelScope.launch(Dispatchers.IO){
        val items = this@ActivityGroupViewModel.activities.value
            .mapIndexed{index, item -> item.activity.copy(groupPosition = index)}
            .toTypedArray()

        db.activityDAO().updateAll(*items)
    }

    fun delete(item: TrackerActivityGroup) = launchIO {
        db.groupDAO().delete(item)
    }

}
