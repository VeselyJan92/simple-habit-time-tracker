package com.janvesely.activitytracker.database

import androidx.compose.ui.graphics.Color
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random.Default.nextInt

class Seeder {

    private fun randomWord(): String{
        val words = arrayOf("Lorem", "Ipsum", "dolor", "sit", "amet")
        return words[Random().nextInt(words.size)]
    }

/*    private fun randomColor(): String{
        val colors = context.resources.getStringArray(R.array.tracked_task_colors)
        return colors[Random().nextInt(colors.size)]
    }*/

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
        goal: Long = 0,
        color: String = Color.Blue.toString(),
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
        score: Long = nextInt(1, 3).toLong()
    )  = TrackedActivityScore(id, activityId, datetime_scored, score)


    fun getTrackedTaskSession(
        id: Long = 0,
        activityId : Long = 0,
        start: LocalDateTime = shiftDateTime(nextInt(0, 2), nextInt(-1, 1)),
        end: LocalDateTime = start.plusHours(1)
    )  = TrackedActivitySession(id, activityId, start, end)


    /*fun getTrackedActivityWithMetric(
        activity: TrackedActivity = getTrackedActivity()
    ): TrackedActivityWithMetric {

        val data =  activity.metric_range.getPastRanges()

        when(activity.type){
            TrackedActivity.Type.SESSION -> data.apply {
                this[1].metric = 60 * 124
                this[2].metric = 60 * 124
                this[4].metric = 60 * 124
            }

            TrackedActivity.Type.SCORE -> data.apply {
                this[1].metric = 2
                this[2].metric = 3
                this[4].metric = 2
            }

            TrackedActivity.Type.COMPLETED -> data.apply {
                this[1].metric = 0
                this[2].metric = 1
                this[4].metric = 1
            }
        }

        return TrackedActivityWithMetric(activity, data)
    }*/

}