package com.janvesely.getitdone.database.entities.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_TIME
import java.util.*

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
