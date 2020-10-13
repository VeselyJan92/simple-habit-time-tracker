package com.janvesely.activitytracker.database.converters

import androidx.room.TypeConverter
import com.janvesely.activitytracker.database.embedable.TimeRange

object TimeRangeConverter {
    @TypeConverter
    fun from(type: TimeRange) = type.toString()

    @TypeConverter
    fun to(name: String) = TimeRange.valueOf(name)
}