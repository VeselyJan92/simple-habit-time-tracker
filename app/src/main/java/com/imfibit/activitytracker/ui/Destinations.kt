package com.imfibit.activitytracker.ui

import kotlinx.serialization.Serializable


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
        val activityId: Long
    )

    @Serializable
    data class ScreenActivityHistory(
        val activityId: Long
    )


    //TODO add in future
   /* @Serializable
    data class DialogActivityDayHistory(
        val activityId: Long,

        @Serializable(with = LocalDateSerializer::class)
        val date: LocalDate
    )

    @Serializable
    data class DialogEditRecord(
        val item: TrackedActivityRecord,
    )
*/


    @Serializable
    data class  ScreenActivityGroupRoute(
        val groupId: Long
    )

    
}