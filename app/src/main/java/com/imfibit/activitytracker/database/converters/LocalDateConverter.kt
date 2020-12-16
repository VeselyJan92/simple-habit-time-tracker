package com.imfibit.activitytracker.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

object LocalDateConverter {

    @TypeConverter
    public fun from(datetime: LocalDate?): String? {
        return datetime?.format(ISO_DATE)
    }

    @TypeConverter
    public fun to(date: String?): LocalDate? {
        return if (date != null) LocalDate.parse(date, ISO_DATE) else null
    }

}
