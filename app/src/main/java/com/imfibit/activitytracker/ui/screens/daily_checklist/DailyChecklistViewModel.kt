package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.lazy.LazyListItemInfo
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.dailyChecklistTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTimeline
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class DailyChecklistViewModel @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTimeline,
) : BaseViewModel() {

    val items = invalidationStateFlow(db, listOf(), *dailyChecklistTables) {
        db.dailyCheckListItemsDao().getAll()
    }

    val days = invalidationStateFlow(db, listOf(), *dailyChecklistTables) {
        rep.getDataForPastDays(30).reversed()
    }

    val history = invalidationStateFlow(db, listOf(), *dailyChecklistTables) {
        rep.getDataForPastDays(LocalDate.now().minusMonths(6L), LocalDate.now()).reversed()
    }

    val strike = invalidationStateFlow(db, 0, *dailyChecklistTables) {
        rep.getStrike()
    }


    fun onCheck(checked: Boolean?, item: DailyChecklistItem) = launchIO {
        rep.checkItem(checked, item)

    }

    fun onToggleDay(checked: Boolean, date: LocalDate) = launchIO {
        rep.toggleDailyChecklistCompletion(checked, date)
    }

    fun onEdit(item: DailyChecklistItem) = launchIO {
        rep.db.dailyCheckListItemsDao().update(item)
    }

    fun onAdd(item: DailyChecklistItem) = launchIO {
        rep.addItem(item)
    }

    fun onDelete(item: DailyChecklistItem) = launchIO {
        rep.deleteItem(item)
    }

    fun onReordered() = launchIO {
        rep.reorderItems(items = items.value)
    }

    fun onSwap(from: LazyListItemInfo, to: LazyListItemInfo) {
        items.swap(from.index - 1, to.index - 1)

        launchIO {
            onReordered()
        }
    }

}
