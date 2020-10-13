package com.imfibit.getitdone.database.entities.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_TIME

@SuppressLint("SimpleDateFormat")
object LocalTimeConverter {

    @TypeConverter
    fun from(datetime: LocalTime?): String? {
        return datetime?.format(ISO_TIME)
    }

    @TypeConverter
    fun to(date: String?): LocalTime? {
        return if (date != null) LocalTime.parse(date, ISO_TIME) else null
    }

}
