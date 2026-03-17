package com.imfibit.activitytracker.ui

import android.os.Parcelable
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

sealed interface AppDestination : Parcelable

object Destinations {

    @Serializable
    @kotlinx.parcelize.Parcelize
    object ScreenStatistics : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    object ScreenActivities : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    object ScreenSettings : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    object ScreenOnboarding : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class ScreenActivity(
        val activityId: Long,
    ) : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class ScreenActivityGroupRoute(
        val groupId: Long,
    ) : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class ScreenFocusBundleDetail(
        val bundleId: Long,
    ) : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class ScreemEditFocusBoardItem(
        val bundleId: Long,
        val noteId: Long
    ) : AppDestination

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class DialogActivityDayHistory(
        val activityId: Long,
        val date: String,
    ) : AppDestination {

        // bit of hack here
        constructor(activityId: Long, date: LocalDate) : this(
            activityId,
            date.toString()
        )

        fun getDate(): LocalDate {
            return LocalDate.parse(date)
        }
    }

    @Serializable
    @kotlinx.parcelize.Parcelize
    data class DialogEditRecord(
        val item: TrackedActivityRecord,
    ) : AppDestination


}
