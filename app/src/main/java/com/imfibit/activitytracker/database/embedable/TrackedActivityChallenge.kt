package com.imfibit.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.imfibit.activitytracker.database.entities.TrackedActivity
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


data class TrackedActivityChallenge(
    @ColumnInfo(name = "challenge_name")
    val name: String,

    @ColumnInfo(name = "challenge_target")
    val target: Long,

    @ColumnInfo(name = "challenge_from")
    val from: LocalDate,

    @ColumnInfo(name = "challenge_to")
    val to: LocalDate
){
    companion object{
        
        val empty = TrackedActivityChallenge(
            name = "",
            target = 0,
            from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }

    fun format(type: TrackedActivity.Type) = when (type){
        TrackedActivity.Type.TIME -> target / 3600
        TrackedActivity.Type.SCORE -> target
        TrackedActivity.Type.CHECKED -> target
    }

    fun isSet() = target != 0L
}