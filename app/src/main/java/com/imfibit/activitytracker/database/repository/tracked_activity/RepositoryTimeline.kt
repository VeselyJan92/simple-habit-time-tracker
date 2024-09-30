package com.imfibit.activitytracker.database.repository.tracked_activity

import androidx.room.withTransaction
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.getFullMonthBlockDays
import com.imfibit.activitytracker.core.toSequence
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
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

    suspend fun getDataForPastMonths(n: Int) = buildList {
        val data = db.dailyCheckListTimelineDAO()
            .getFromTo(LocalDate.now().minusMonths(n + 1L), LocalDate.now())
            .map { it.date_completed }.toSet()

        repeat(n) {
            add(mapMonthData(YearMonth.now().minusMonths(it.toLong()), data))
        }
    }

    suspend fun getMonthData(month: YearMonth): RepositoryTrackedActivity.Month {
        val days = getFullMonthBlockDays(month.year, month.monthValue)

        val set = db.dailyCheckListTimelineDAO()
            .getFromTo(days.lower, days.upper)
            .map { it.date_completed }.toSet()

        return mapMonthData(month, set)
    }

    suspend fun mapMonthData(
        month: YearMonth,
        data: Set<LocalDate>
    ): RepositoryTrackedActivity.Month {
        val days = getFullMonthBlockDays(month.year, month.monthValue)

        val weeks = days.toSequence().chunked(7).map { week ->
            RepositoryTrackedActivity.Week(
                week.first(),
                week.last(),
                week.map {
                    val completed = data.contains(it)

                    RepositoryTrackedActivity.Day(
                        { resources.getString(if (completed) R.string.yes else R.string.no) },
                        if (completed) 1 else 0,
                        if (completed) Colors.ButtonGreen else Colors.ChipGray,
                        it
                    )
                },
                week.count { data.contains(it) }.toLong()
            )
        }

        return RepositoryTrackedActivity.Month(weeks.toList(), month)
    }


}
