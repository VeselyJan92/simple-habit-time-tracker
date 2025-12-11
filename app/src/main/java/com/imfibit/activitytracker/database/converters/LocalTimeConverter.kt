package com.imfibit.activitytracker.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalTime

class LocalTimeConverter {

    @TypeConverter
    fun from(datetime: LocalTime?): String? {
        return datetime?.toString()
    }

    @TypeConverter
    fun to(date: String?): LocalTime? {
        return if (date != null) LocalTime.parse(date) else null
    }

}
