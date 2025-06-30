package com.imfibit.activitytracker.database.repository.tracked_activity

import androidx.core.util.rangeTo
import androidx.room.withTransaction
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.getFullMonthBlockDays
import com.imfibit.activitytracker.core.toSequence
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.ui.components.Colors
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

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
            item.copy(date_checked = if (checked == true) LocalDate.now() else null)
        )

        checklistCheckTodayCompleted()
    }

    suspend fun checklistCheckTodayCompleted() {
        val today = LocalDate.now()

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
        val from = LocalDate.now().minusDays(n.toLong() -1 )
        val to = LocalDate.now()

        val completed =  db.dailyCheckListTimelineDAO().getFromTo(from, to).map { it.date_completed }.toSet()

        return (from rangeTo to).toSequence().map { DailyChecklistTimelineItemValue(it, completed.contains(it))  }.toList()
    }

    suspend fun getDataForPastDays(from:  LocalDate, to: LocalDate): List<DailyChecklistTimelineItemValue> {
        val completed =  db.dailyCheckListTimelineDAO().getFromTo(from, to).map { it.date_completed }.toSet()

        return (from rangeTo to).toSequence().map { DailyChecklistTimelineItemValue(it, completed.contains(it))  }.toList()
    }

    suspend fun getStrike(): Int {
        return db.dailyCheckListTimelineDAO().getStrike()
    }

}

