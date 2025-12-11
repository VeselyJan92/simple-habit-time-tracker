package com.imfibit.activitytracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Entity(
    tableName = DailyChecklistTimelineItem.TABLE,
    indices = [
        Index(value = ["date_completed"], name = "date_completed_pk"),
    ],
)
data class DailyChecklistTimelineItem(
    @PrimaryKey
    @ColumnInfo(name = "date_completed")
    val date_completed: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    companion object {
        const val TABLE = "daily_checklist_timeline"
    }
}

data class DailyChecklistTimelineItemValue(
    val  date_completed: LocalDate,
    val  completed: Boolean,
)
