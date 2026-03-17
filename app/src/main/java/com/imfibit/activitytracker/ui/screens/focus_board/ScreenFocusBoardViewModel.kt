package com.imfibit.activitytracker.ui.screens.focus_board

import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.embedable.Markdown
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.database.focusBoardTables
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.core.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenFocusBoardViewModel @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryFocusBoard,
    private val navigator: AppNavigator
) : BaseViewModel() {

    data class Data(
        val bundles: List<FocusBundle>,
        val staredItems: List<FocusBoardItemWithTags>
    )

    val data = invalidationStateFlow<Data?>(db, null, *focusBoardTables) {
        Data(
            bundles = rep.getBundles(),
            staredItems = rep.getDashboardItems()
        )
    }

    fun swapBundles(fromBundleId: Long, toBundleId: Long) {
        val current = data.value ?: return
        val list = current.bundles.toMutableList()
        val fromIndex = list.indexOfFirst { it.id == fromBundleId }
        val toIndex = list.indexOfFirst { it.id == toBundleId }

        if (fromIndex != -1 && toIndex != -1) {
            list.swap(fromIndex, toIndex)
            data.value = current.copy(bundles = list)

            launchIO {
                rep.updateBundles(list.mapIndexed { index, item -> item.copy(position = index) })
            }
        }
    }

    fun swapStarredItems(fromItemId: Long, toItemId: Long) {
        val current = data.value ?: return
        val list = current.staredItems.toMutableList()
        val fromIndex = list.indexOfFirst { it.item.id == fromItemId }
        val toIndex = list.indexOfFirst { it.item.id == toItemId }

        if (fromIndex != -1 && toIndex != -1) {
            list.swap(fromIndex, toIndex)
            data.value = current.copy(staredItems = list) // Optimistic update

            launchIO {
                rep.updateFocusBoardItems(list.mapIndexed { index, item -> item.item.copy(dashboardPosition = index) })
            }
        }
    }

    fun addNote(bundleId: Long) = launchIO {
        val noteId = rep.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundleId, title = "", content = Markdown("")),
            emptyList()
        )
        navigator.navigate(Destinations.ScreenFocusBundleDetail(bundleId))
        navigator.navigate(Destinations.ScreemEditFocusBoardItem(bundleId, noteId))
    }

    fun toggleNoteCompletion(item: FocusBoardItem) = launchIO {
        rep.toggleFocusItemCompletion(item.id)
    }
}
