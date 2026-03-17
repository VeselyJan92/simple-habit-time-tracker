package com.imfibit.activitytracker.ui.screens.dashboard

import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.navigation.AppNavigator
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.focusBoardTables
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.repository.tracked_activity.DailyChecklistRepository
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val db: AppDatabase,
    private val repTrackedActivity: RepositoryTrackedActivity,
    private val repDailyChecklist: DailyChecklistRepository,
    private val repFocusBoard: RepositoryFocusBoard,
    private val navigation: AppNavigator,
) : BaseViewModel() {

    fun createNewActivity(name: String, type: TrackedActivity.Type) {
        val activity = TrackedActivity(
            id = 0L,
            name = name,
            position = 0,
            type = type,
            inSessionSince = null,
            goal = TrackedActivityGoal(0L, TimeRange.WEEKLY),
            challenge = TrackedActivityChallenge.empty
        )

        viewModelScope.launch(Dispatchers.IO) {
            val id = repTrackedActivity.activityDAO.insert(activity)
            navigation.navigate(Destinations.ScreenActivity(id))
        }
    }

    fun addGroup(group: TrackerActivityGroup) = launchIO {
        db.groupDAO().insert(group)
    }

    fun addDailyChecklistItem(item: DailyChecklistItem) = launchIO {
        repDailyChecklist.addItem(item)
    }

    fun createNewBundle(bundle: FocusBundle, tags: List<FocusBoardItemTag>) = launchIO {
        repFocusBoard.createBundle(bundle, tags)
    }
}
