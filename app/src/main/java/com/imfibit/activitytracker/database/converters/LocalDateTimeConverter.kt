package com.janvesely.activitytracker.database.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*

@SuppressLint("SimpleDateFormat")
object LocalDateTimeConverter {

    @TypeConverter
    fun from(datetime: LocalDateTime?): String? {
        return datetime?.format(ISO_DATE_TIME)
    }

    @TypeConverter
    fun to(date: String?): LocalDateTime? {
        return if (date != null) LocalDateTime.parse(date, ISO_DATE_TIME) else null
    }

}
