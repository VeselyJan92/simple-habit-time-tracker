package com.imfibit.activitytracker.ui.screens.tracked_activities.group

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.activityTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.core.navigation.AppNavigator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel(assistedFactory = ActivityGroupViewModel.Factory::class)
class ActivityGroupViewModel @AssistedInject constructor(
    private val rep: RepositoryTrackedActivity,
    private val db: AppDatabase,
    private val navigation: AppNavigator,
    @Assisted private val groupId: Long
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(groupId: Long): ActivityGroupViewModel
    }

    //For better edittext performance save the name of the activity when user is done with the screen
    val groupName = mutableStateOf<String?>(null)


    val activities = invalidationStateFlow(db, listOf(), *activityTables){
        rep.getActivitiesOverview(db.activityDAO().getActivitiesFromGroup(groupId))
    }

    val group = invalidationStateFlow(db, null, *activityTables){
        val group = db.groupDAO().getByIdOrNull(groupId)

        if (group != null){
            viewModelScope.launch(Dispatchers.Main) {
                groupName.value = group.name
            }
        }

        group
    }


    override fun onCleared()  = runBlocking(Dispatchers.IO) {
        val group = db.groupDAO().getByIdOrNull(groupId)

        // If name is not filled or group was deleted
        if (!groupName.value.isNullOrBlank() && group != null)
            db.groupDAO().update(group.copy(name = groupName.value!!))

    }

    fun refreshName(name: String){
        this.groupName.value = name
    }

    fun onMoveActivity(from: LazyListItemInfo, to: LazyListItemInfo) {
        this.activities.value = this.activities.value.toMutableList().apply { swap(from.index, to.index) }

        launchIO {
            val items = this@ActivityGroupViewModel.activities.value
                .mapIndexed{index, item -> item.activity.copy(groupPosition = index)}
                .toTypedArray()

            db.activityDAO().updateAll(*items)
        }
    }

    fun delete(item: TrackerActivityGroup) = launchIO {
        db.groupDAO().delete(item)
    }

    fun createNewActivity(name: String, type: TrackedActivity.Type) {
        val activity = TrackedActivity(
            id = 0L,
            groupId = groupId,
            name = name,
            position = 0,
            type = type,
            inSessionSince = null,
            goal = TrackedActivityGoal(0L, TimeRange.WEEKLY),
            challenge = TrackedActivityChallenge.empty
        )

        viewModelScope.launch(Dispatchers.IO) {
            val id = rep.activityDAO.insert(activity)
            navigation.navigate(Destinations.ScreenActivity(id))
        }
    }

}
