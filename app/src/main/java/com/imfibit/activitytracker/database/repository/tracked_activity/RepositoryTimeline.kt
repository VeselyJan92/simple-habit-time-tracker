package com.imfibit.activitytracker.database.repository.tracked_activity

import androidx.core.util.rangeTo
import androidx.room.withTransaction
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.getFullMonthBlockDays
import com.imfibit.activitytracker.core.iter
import com.imfibit.activitytracker.core.toSequence
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.ui.components.Colors
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@Module
@InstallIn(SingletonComponent::class)
class RepositoryTimeline @Inject constructor(
    val db: AppDatabase
) {
    suspend fun reorderItems(items: List<DailyChecklistItem>) = db.withTransaction {
        items.mapIndexed { index, item -> item.copy(position = index) }.forEach {
            db.dailyCheckListItemsDao().update(it)
        }
    }

    suspend fun addItem(item: DailyChecklistItem) {
        db.dailyCheckListItemsDao().insert(item)

        checklistCheckTodayCompleted()
    }

    suspend fun deleteItem(item: DailyChecklistItem) {
        db.dailyCheckListItemsDao().delete(item)

        checklistCheckTodayCompleted()
    }

    suspend fun checkItem(checked: Boolean?, item: DailyChecklistItem) = db.withTransaction {
        db.dailyCheckListItemsDao().update(
            item.copy(date_checked = if (checked == true) Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date else null)
        )

        checklistCheckTodayCompleted()
    }

    
    suspend fun checklistCheckTodayCompleted() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val allChecked = db.dailyCheckListItemsDao().getAll().all {
            it.date_checked == today
        }

        toggleDailyChecklistCompletion(allChecked, today)
    }

    suspend fun toggleDailyChecklistCompletion(
        checked: Boolean,
        date: LocalDate
    ) {
        val item = DailyChecklistTimelineItem(date)

        if (checked) {
            db.dailyCheckListTimelineDAO().upsert(item)
        } else {
            db.dailyCheckListTimelineDAO().delete(item)
        }
    }

    suspend fun getDataForPastDays(n: Int): List<DailyChecklistTimelineItemValue> {
        val to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val from = to.minus(n - 1, DateTimeUnit.DAY)

        val completed =  db.dailyCheckListTimelineDAO().getFromTo(from, to).map { it.date_completed }.toSet()

        return (from iter to).asSequence().map { DailyChecklistTimelineItemValue(it, completed.contains(it))  }.toList()
    }

    suspend fun getDataForPastDays(from:  LocalDate, to: LocalDate): List<DailyChecklistTimelineItemValue> {
        val completed =  db.dailyCheckListTimelineDAO().getFromTo(from, to).map { it.date_completed }.toSet()

        return (from iter to).asSequence().map { DailyChecklistTimelineItemValue(it, completed.contains(it))  }.toList()
    }

    suspend fun getStrike(): Int {
        return db.dailyCheckListTimelineDAO().getStrike()
    }

}
