package com.imfibit.activitytracker.ui

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object Destinations {

    @Serializable
    object ScreenStatistics

    @Serializable
    object ScreenActivities

    @Serializable
    object ScreenSettings

    @Serializable
    object ScreenOnboarding

    @Serializable
    data class ScreenActivity(
        val activityId: Long,
    )

    @Serializable
    data class ScreenActivityGroupRoute(
        val groupId: Long,
    )

    @Serializable
    data class DialogActivityDayHistory(
        val activityId: Long,
        val date: String,
    ) {

        // bit of hack here
        constructor(activityId: Long, date: LocalDate) : this(
            activityId,
            date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )

        fun getDate(): LocalDate {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

//    @Serializable
//    data class DialogEditRecord(
//        val item: TrackedActivityRecord,
//    )


}