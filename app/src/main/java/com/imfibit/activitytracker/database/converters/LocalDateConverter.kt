package com.janvesely.activitytracker.database.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*

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
