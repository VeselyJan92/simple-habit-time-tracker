package com.imfibit.activitytracker.database.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

@SuppressLint("SimpleDateFormat")
object LocalDateConverter {

    @TypeConverter
    @JvmStatic
    fun from(datetime: LocalDate?): String? {
        return datetime?.format(ISO_DATE)
    }

    @TypeConverter @JvmStatic
    fun to(date: String?): LocalDate? {
        return if (date != null) LocalDate.parse(date, ISO_DATE) else null
    }

}
