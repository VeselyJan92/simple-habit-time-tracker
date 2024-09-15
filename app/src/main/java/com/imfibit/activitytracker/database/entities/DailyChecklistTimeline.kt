package com.imfibit.activitytracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(
    tableName = DailyChecklistTimelineItem.TABLE,
    indices = [
        Index(value = ["date_completed"], name = "date_completed_pk"),
    ],
)
data class DailyChecklistTimelineItem(
    @PrimaryKey
    @ColumnInfo(name = "date_completed")
    val date_completed: LocalDate = LocalDate.now(),
) {
    companion object {
        const val TABLE = "daily_checklist_timeline"
    }
}
