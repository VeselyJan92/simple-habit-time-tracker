package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.extensions.swapByIndex
import com.imfibit.activitytracker.core.invalidationFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import javax.inject.Inject

import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBoardItemTagRelation
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import kotlinx.coroutines.flow.collectLatest


@HiltViewModel
class FocusBoardViewModel @Inject constructor(
    private val db: AppDatabase,
    private val  rep: RepositoryFocusBoard
) : AppViewModel() {

    val tags = mutableStateListOf<FocusBoardItemTag>()

    val focusItems = mutableStateListOf<FocusBoardItemWithTags>()

    init {
        viewModelScope.launch {
            db.focusBoardItemTagDAO().getAllFlow().collect {
                tags.clear()
                tags.addAll(it)
            }
        }

        //NEED test background updates
        viewModelScope.launch {
            val focusItemsFlow = invalidationFlow(db, FocusBoardItem.TABLE, FocusBoardItemTag.TABLE, FocusBoardItemTagRelation.TABLE){
                rep.getFocusItemsWithTags()
            }

            focusItemsFlow.collect {
                focusItems.clear()
                focusItems.addAll(it)
            }
        }
    }

    fun reorderFocusItems() = launchIO {
        val timers = focusItems.mapIndexed{index, item -> item.item.copy(position = index) }.toTypedArray()
        db.focusBoardItemDAO().updateAll(*timers)
    }

    fun swapFocusItems(from: ItemPosition, to: ItemPosition) {
        this.focusItems.swapByIndex(
            focusItems.indexOfFirst { from.key == it.item.id },
            focusItems.indexOfFirst { to.key == it.item.id }
        )
    }

    fun reorderTags() = launchIO {
        val timers = tags.mapIndexed{index, item -> item.copy(position = index) }.toTypedArray()
        db.focusBoardItemTagDAO().updateAll(*timers)
    }

    fun swapTags(from: ItemPosition, to: ItemPosition) {
        this.tags.swap(from, to)
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
