package com.imfibit.activitytracker.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun from(datetime: LocalDate?): String? {
        return datetime?.toString()
    }

    @TypeConverter
    fun to(date: String?): LocalDate? {
        return if (date != null) LocalDate.parse(date) else null
    }

}
