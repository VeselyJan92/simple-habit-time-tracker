package com.imfibit.activitytracker.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

class LocalDateTimeConverter {

    @TypeConverter
    fun from(datetime: LocalDateTime?): String? {
        return datetime?.toString()
    }

    @TypeConverter
    fun to(date: String?): LocalDateTime? {
        return if (date != null) LocalDateTime.parse(date) else null
    }

}
