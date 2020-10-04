package com.janvesely.activitytracker.database.embedable


data class TrackedActivityGoal(
    val value: Long,
    val range: TimeRange
){
    fun isSet() = value != 0L
}