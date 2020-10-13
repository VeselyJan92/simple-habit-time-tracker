package com.imfibit.getitdone.database.entities.converters

import androidx.room.TypeConverter
import com.imfibit.activitytracker.database.entities.TrackedActivity

object TrackedActivityTypeConverter {
    @TypeConverter
    @JvmStatic
    fun from(type: TrackedActivity.Type): String = type.toString()

    @TypeConverter @JvmStatic
    fun to(ordinal: String): TrackedActivity.Type = TrackedActivity.Type.valueOf(ordinal)
}