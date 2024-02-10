package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.extensions.swapByIndex
import com.imfibit.activitytracker.core.focusBoardTables
import com.imfibit.activitytracker.core.invalidationStateFlow
import com.imfibit.activitytracker.core.registerInvalidationTracker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import javax.inject.Inject


@HiltViewModel
class FocusBoardViewModel @Inject constructor(
    private val db: AppDatabase,
    private val  rep: RepositoryFocusBoard
) : AppViewModel() {

    val tags = invalidationStateFlow(db, listOf(),*focusBoardTables){
        db.focusBoardItemTagDAO().getAll()
    }

    val focusItems = invalidationStateFlow(db, listOf(),*focusBoardTables){
        rep.getFocusItemsWithTags()
    }

    fun reorderFocusItems() = launchIO {
        val timers = focusItems.value.mapIndexed{index, item -> item.item.copy(position = index) }.toTypedArray()
        db.focusBoardItemDAO().updateAll(*timers)
    }

    fun swapFocusItems(from: ItemPosition, to: ItemPosition) {
        focusItems.swap(from, to)
    }

    fun reorderTags() = launchIO {
        val timers = tags.value.mapIndexed{index, item -> item.copy(position = index) }.toTypedArray()
        db.focusBoardItemTagDAO().updateAll(*timers)
    }

    fun swapTags(from: ItemPosition, to: ItemPosition) {
        tags.swap(from, to)
    }

    fun onFocusItemEdit(item: FocusBoardItemWithTags) = launchIO {
        rep.updateFocusItem(item)
    }

    fun onFocusItemDelete(item: FocusBoardItemWithTags) = launchIO {
        rep.deleteFocusItem(item.item)
    }

    fun createNewFocusItem(item: FocusBoardItemWithTags) = launchIO {
        rep.insertFocusItemWithTags(item.item,  item.tags)
    }

    fun onTagEdit(item: FocusBoardItemTag) = launchIO {
        db.focusBoardItemTagDAO().upsert(item)
    }

    fun onTagDelete(item: FocusBoardItemTag) = launchIO  {
        db.focusBoardItemTagDAO().delete(item)
    }


}
