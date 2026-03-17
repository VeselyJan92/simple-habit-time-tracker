package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.lazy.LazyListItemInfo
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.dailyChecklistTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.database.repository.tracked_activity.DailyChecklistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock

@HiltViewModel
class DailyChecklistViewModel @Inject constructor(
    private val db: AppDatabase,
    private val rep: DailyChecklistRepository,
) : BaseViewModel() {

    data class Data(
        val items: List<DailyChecklistItem>,
        val days: List<DailyChecklistTimelineItemValue>,
        val history: List<DailyChecklistTimelineItemValue>,
        val strike: Int
    )

    val data = invalidationStateFlow<Data?>(db, null, *dailyChecklistTables) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        Data(
            items = db.dailyCheckListItemsDao().getAll(),
            days = rep.getDataForPastDays(30).reversed(),
            history = rep.getDataForPastDays(now.minus(6, DateTimeUnit.MONTH), now).reversed(),
            strike = rep.getStrike()
        )
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

    fun onDelete(item: DailyChecklistItem) = launchIO {
        rep.deleteItem(item)
    }

    fun onSwap(from: LazyListItemInfo, to: LazyListItemInfo) {
        val current = data.value ?: return
        val items = current.items.toMutableList().apply { swap(from.index - 1, to.index - 1) }
        data.value = current.copy(items = items)

        launchIO {
            rep.reorderItems(items = items)
        }
    }

}
