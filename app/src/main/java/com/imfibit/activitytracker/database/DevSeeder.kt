package com.imfibit.activitytracker.database

import androidx.compose.ui.graphics.toArgb
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random.Default.nextInt

object DevSeeder {

    public fun getTags() = listOf(
        FocusBoardItemTag(id = 1, name = "Habits", color = FocusBoardItemTag.colors[0].toArgb()),
        FocusBoardItemTag(id = 2, name = "Tasks", color = FocusBoardItemTag.colors[1].toArgb()),
        FocusBoardItemTag(id = 3, name = "Side Quests", color = FocusBoardItemTag.colors[2].toArgb())
    )

    public fun getFocusBoardItemTag() = getTags()[0]

    public fun getFocusBoardItems() = listOf(

        FocusBoardItem(
            id = 3,
            title = "Business and trends research",
            content = "Google doc of business research: \n" + "• Trends, industry, research" + "\n" + "• Technology" + "\n" + "• Understanding business models",
        ),

        FocusBoardItem(
            id = 1,
            title = "Working out"
        ),

        FocusBoardItem(
            id = 2,
            title = "Learning spanish"
        ),

        FocusBoardItem(
            id = 4,
            title = "My book list",
            content = "Atomic habits: \n" + "• The 7 Habits of Highly Effective People" + "\n" + "• The Richest Man in Babylon",
        ),
    )

    public fun getFocusBoardItem() = getFocusBoardItems()[0]


    public fun getFocusItemWithTags() = FocusBoardItemWithTags(getFocusBoardItem(), getTags().take(2))



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

/*    fun getTrackedActivity(
        id: Long = 0,
        name: String = randomWord(),
        position: Int = nextInt(100, 200),
        type: TrackedActivity.Type = TrackedActivity.Type.CHECKED,
        inSession: LocalDateTime? = null,
        range: TimeRange = TimeRange.DAILY,
        goal: Long = 0
    ) = TrackedActivity(id, null, name, position, type, inSession, TrackedActivityGoal(goal, range))*/


    fun getTrackedTaskCompletion(
        id: Long = 0,
        activityId : Long = 0,
        date: LocalDateTime = shiftDateTime(nextInt(0, 2))
    )  = TrackedActivityCompletion(id, activityId, date.toLocalDate(), date.toLocalTime())

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
    )  = TrackedActivityTime(id, activityId, start, end)


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