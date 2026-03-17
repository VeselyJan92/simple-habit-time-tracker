package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.lazy.LazyListItemInfo
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.navigation.AppNavigator
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.embedable.Markdown
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.database.focusBoardTables
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import com.imfibit.activitytracker.ui.Destinations
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ScreenFocusBundleViewModel.Factory::class)
class ScreenFocusBundleViewModel @AssistedInject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryFocusBoard,
    private val navigator: AppNavigator,
    @Assisted val bundleId: Long
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(bundleId: Long): ScreenFocusBundleViewModel
    }

    data class Data(
        val bundle: FocusBundle,
        val tags: List<FocusBoardItemTag>,
        val focusItems: List<FocusBoardItemWithTags>
    )

    val data = invalidationStateFlow<Data?>(db, null, *focusBoardTables) {
        val bundle = db.focusBundleDAO().getById(bundleId) ?: return@invalidationStateFlow null
        Data(
            bundle = bundle,
            tags = db.focusBoardItemTagDAO().getAllByBundle(bundleId),
            focusItems = rep.getFocusItemsWithTags(bundleId)
        )
    }

    fun swapFocusItems(from: LazyListItemInfo, to: LazyListItemInfo) {
        val fromId = from.key as? Long ?: return
        val toId = to.key as? Long ?: return

        val currentData = data.value ?: return
        val currentList = currentData.focusItems.toMutableList()
        val fromIndex = currentList.indexOfFirst { it.item.id == fromId }
        val toIndex = currentList.indexOfFirst { it.item.id == toId }

        if (fromIndex == -1 || toIndex == -1) return

        val fromItem = currentList[fromIndex].item
        val toItem = currentList[toIndex].item

        // Prevent dragging between different sections (e.g. pinned <-> active <-> completed)
        if (fromItem.isPinned != toItem.isPinned || fromItem.isCompleted != toItem.isCompleted) {
            return
        }

        val temp = currentList[fromIndex]
        currentList[fromIndex] = currentList[toIndex]
        currentList[toIndex] = temp

        data.value = currentData.copy(focusItems = currentList)

        launchIO {
            val timers =
                currentList.mapIndexed { index, item -> item.item.copy(bundlePosition = index) }
                    .toTypedArray()
            db.focusBoardItemDAO().updateAll(*timers)
        }
    }

    fun onBundleSave(bundle: FocusBundle, tags: List<FocusBoardItemTag>) = launchIO {
        rep.saveBundle(bundle, tags)
    }

    fun onTagToggle(item: FocusBoardItemTag) = launchIO {
        db.focusBoardItemTagDAO().upsert(item.copy(isChecked = !item.isChecked))
    }
    

    fun onBundleDelete(bundle: FocusBundle) = launchIO {
        rep.deleteBundle(bundle)
    }

    fun onCompleteToggle(item: FocusBoardItemWithTags) = launchIO {
        rep.toggleFocusItemCompletion(item.item.id)
    }

    fun addFocusItem() = launchIO {
        val noteId = rep.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundleId, title = "", content = Markdown("")),
            emptyList()
        )
        navigator.navigate(Destinations.ScreemEditFocusBoardItem(bundleId, noteId))
    }
}
