package com.janvesely.activitytracker.database

import android.content.Context
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import java.text.FieldPosition
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random.Default.nextInt

class Seeder(private val context: Context) {

    private fun randomWord(): String{
        val words = arrayOf("Lorem", "Ipsum", "dolor", "sit", "amet")
        return words[Random().nextInt(words.size)]
    }

    private fun randomColor(): String{
        val colors = context.resources.getStringArray(R.array.tracked_task_colors)
        return colors[Random().nextInt(colors.size)]
    }

   /* private fun randomIcon(): String {
        val icons = context.resources.getStringArray(R.array.tracked_task_icons)
        return icons[Random().nextInt(icons.size)]
    }*/

    private inline fun <reified T: Enum<T>> randomEnum() = enumValues<T>().random()

    fun shiftDateTime(addHours:Int = 0, addDays: Int = 0): LocalDateTime {
        var datetime = LocalDateTime.now()
        datetime = datetime.plusHours(addHours.toLong())
        datetime = datetime.plusDays(addDays.toLong())
        return datetime
    }

    fun getTrackedActivity(
        id: Long = 0,
        name: String = randomWord(),
        position: Int = nextInt(100, 200),
        type: TrackedActivity.Type = TrackedActivity.Type.COMPLETED,
        inSession: LocalDateTime? = null,
        range: TimeRange = TimeRange.DAILY,
        goal: Int = 10,
        color: String = randomColor(),
        icon: String = ""
    ) = TrackedActivity(id, name, position, type, inSession, range, goal, color, icon)


    fun getTrackedTaskCompletion(
        id: Long = 0,
        activityId : Long = 0,
        date: LocalDate = shiftDateTime(nextInt(0, 2)).toLocalDate()
    )  = TrackedActivityCompletion(id, activityId, date)

    fun getTrackedTaskScore(
        id: Long = 0,
        activityId : Long = 0,
        datetime_scored: LocalDateTime = shiftDateTime(nextInt(0, 2), nextInt(-1, 1)),
        score: Int = nextInt(1, 3)
    )  = TrackedActivityScore(id, activityId, datetime_scored, score)


    fun getTrackedTaskSession(
        id: Long = 0,
        activityId : Long = 0,
        start: LocalDateTime = shiftDateTime(nextInt(0, 2), nextInt(-1, 1)),
        end: LocalDateTime = start.plusHours(1)
    )  = TrackedActivitySession(id, activityId, start, end)

}