package com.imfibit.activitytracker.database.converters

import androidx.room.TypeConverter
import com.imfibit.activitytracker.database.entities.TrackedActivity

object TrackedActivityTypeConverter {
    @TypeConverter
    fun from(type: TrackedActivity.Type): String = type.toString()

    @TypeConverter
    fun to(ordinal: String): TrackedActivity.Type = TrackedActivity.Type.valueOf(ordinal)
}