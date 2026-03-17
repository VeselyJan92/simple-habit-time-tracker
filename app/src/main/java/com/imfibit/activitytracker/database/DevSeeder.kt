package com.imfibit.activitytracker.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.imfibit.activitytracker.core.extensions.getFullMonthBlockDays
import com.imfibit.activitytracker.core.extensions.iter
import com.imfibit.activitytracker.core.extensions.toSequence
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.embedable.Markdown
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors
import com.imfibit.activitytracker.core.enums.MetricStatus
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.*
import kotlin.random.Random.Default.nextInt

object DevSeeder {

    fun getPresetTimer(
        id: Long = 0,
        activity_id: Long = 0,
        seconds: Int = 60 * 30,
        position: Int = 0
    ) = PresetTimer( id, activity_id, seconds, position)


    fun getDailyChecklistItem() = DailyChecklistItem(title = "Workout", color = chooseableColors[4].toArgb(), description = "Plan your workout")

    fun getDailyChecklistTimelineCompletions(): List<DailyChecklistTimelineItem> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return (today.minus(180, DateTimeUnit.DAY) iter today).asSequence().mapNotNull {
            if (kotlin.random.Random.nextBoolean()){
                DailyChecklistTimelineItem(it)
            }else {
                null
            }
        }.toList()
    }

    fun getMonthData(year: Int, month: Int): RepositoryTrackedActivity.Month {
        val weeks = getFullMonthBlockDays(year, month).toSequence().chunked(7).map {
            RepositoryTrackedActivity.Week(
                from = it.first(),
                to = it.last(),
                days = it.map {
                    val ok = kotlin.random.Random.nextBoolean()
                    RepositoryTrackedActivity.Day(
                        label = { if (ok) "YES" else "NO" },
                        metric = 1,
                        status = if (ok) MetricStatus.COMPLETED else MetricStatus.DEFAULT,
                        date = it,
                    )
                },
                total = 7
            )
        }.toList()

        return RepositoryTrackedActivity.Month(weeks, year, month)
    }

    // Overload for LocalDate to match call site usage
    fun getMonthData(date: LocalDate): RepositoryTrackedActivity.Month {
        return getMonthData(date.year, date.monthNumber)
    }

    fun getActivityGroup(
        id: Long = 0,
        name: String = "Name",
        position: Int = 0
    ) = TrackerActivityGroup(
        id = id,
        name = name,
        position = position
    )

    fun getTrackedActivityTime(
        id: Long = -1,
        groupId: Long = -1,
        name: String = "Test activity",
        position: Int = 0,
        groupPosition: Int = 0,
        type: TrackedActivity.Type = TrackedActivity.Type.TIME,
        inSessionSince: LocalDateTime? = null,
        goal: TrackedActivityGoal = TrackedActivityGoal(60 * 60, TimeRange.DAILY),
        challenge: TrackedActivityChallenge = TrackedActivityChallenge(
            name = "Research", target = 60 * 60 * 10, from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date, to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(10, DateTimeUnit.DAY)
        )
    ) = TrackedActivity(
        id = id,
        groupId = groupId,
        name = name,
        position = position,
        groupPosition = groupPosition,
        type = type,
        inSessionSince = inSessionSince,
        goal = goal,
        challenge = challenge
    )

    fun getTrackedActivityCompletion(
        id: Long = -1,
        groupId: Long = -1,
        name: String = "Test activity",
        position: Int = 0,
        groupPosition: Int = 0,
        type: TrackedActivity.Type = TrackedActivity.Type.CHECKED,
        inSessionSince: LocalDateTime? = null,
        goal: TrackedActivityGoal = TrackedActivityGoal(0, TimeRange.DAILY),
        challenge: TrackedActivityChallenge = TrackedActivityChallenge(
            name = "", target = -1, from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date, to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    ) = TrackedActivity(
        id = id,
        groupId = groupId,
        name = name,
        position = position,
        groupPosition = groupPosition,
        type = type,
        inSessionSince = inSessionSince,
        goal = goal,
        challenge = challenge
    )

    fun getTrackedActivityScore() = TrackedActivity(
        id = -1,
        groupId = -1,
        name = "Test activity",
        position = 0,
        groupPosition = 0,
        type = TrackedActivity.Type.SCORE,
        inSessionSince = null,
        goal = TrackedActivityGoal(0, TimeRange.DAILY),
        challenge = TrackedActivityChallenge(
            name = "", target = -1, from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date, to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    )

    public fun getTags() = listOf(
        FocusBoardItemTag(id = 1, name = "Habits", color = chooseableColors[3].toArgb()),
        FocusBoardItemTag(id = 2, name = "Tasks", color = chooseableColors[6].toArgb()),
        FocusBoardItemTag(
            id = 3,
            name = "Side Quests",
            color = chooseableColors[2].toArgb()
        )
    )

    public fun getFocusBoardItemTag() = getTags()[0]

    fun getFocusBundle(
        id: Long = 1,
        title: String = "Backlog",
        color: Long = chooseableColors[7].toArgb().toLong(),
        position: Int = 0
    ) = FocusBundle(id, title, color, position)

    fun getFocusBundles() = listOf(
        getFocusBundle(id = 1, title = "Personal Life", color = chooseableColors[0].toArgb().toLong(), position = 0),
        getFocusBundle(id = 2, title = "Work Projects", color = chooseableColors[3].toArgb().toLong(), position = 1),
        getFocusBundle(id = 3, title = "Fitness Goals", color = chooseableColors[6].toArgb().toLong(), position = 2),
        getFocusBundle(id = 4, title = "Learning & Courses", color = chooseableColors[7].toArgb().toLong(), position = 3)
    )

    public fun getMarkdownSample(): String {
        return "# Research topics\n" +
        "**Market Trends**\n" +
                "- Competitor analysis\n" +
                "- Tech innovation\n" +
                "- /Future predictions/\n\n" +
                "### Business Models\n" +
                "1. SaaS\n" +
                "2. Marketplace\n" +
                "3. E-commerce"
    }

    public fun getFocusBoardItems() = listOf(
        getFocusBoardItem(
            id = 3,
            bundleId = 1,
            title = "Business research",
            content = Markdown("# Research topics\n" +
                    "**Market Trends**\n" +
                    "- Competitor analysis\n" +
                    "- Tech innovation\n" +
                    "- /Future predictions/\n\n" +
                    "### Business Models\n" +
                    "1. SaaS\n" +
                    "2. Marketplace\n" +
                    "3. E-commerce"),
        ),
        getFocusBoardItem(
            id = 1,
            bundleId = 1,
            title = "Working out",
            content = Markdown("## Weekly plan\n" +
                    "- **Monday**: Push day\n" +
                    "- **Tuesday**: Pull day\n" +
                    "- ~~Wednesday~~: Rest\n" +
                    "- **Thursday**: Leg day"),
        ),
        getFocusBoardItem(
            id = 2,
            bundleId = 1,
            title = "Learning spanish",
            content = Markdown("### Important phrases\n" +
                    "- /Hola, ¿cómo estás?/\n" +
                    "- Me llamo...\n" +
                    "- ¿Dónde está el baño?\n" +
                    "- ~~Hasta luego~~"),
        ),
        getFocusBoardItem(
            id = 4,
            bundleId = 1,
            title = "My book list",
            content = Markdown("# Must read\n" +
                    "- **Atomic Habits** by James Clear\n" +
                    "- **The 7 Habits** of Highly Effective People\n" +
                    "- /The Richest Man in Babylon/\n" +
                    "- ~~Bad books list~~"),
        ),
    )

    public fun getFocusBoardItem(
        id: Long = 3,
        bundleId: Long = 1,
        title: String = "Business research",
        content: Markdown = Markdown("# Research topics...")
    ) = FocusBoardItem(id, bundleId, title, content)


    public fun getFocusItemWithTags() =
        FocusBoardItemWithTags(getFocusBoardItems()[0], getTags().take(2))

    fun getPinnedNotes(): List<FocusBoardItemWithTags> {
        val items = getFocusBoardItems()
        val tags = getTags()
        return listOf(
            FocusBoardItemWithTags(
                item = items[0].copy(title = "Buy groceries", content = Markdown("- Milk\n- Eggs\n- Bread\n- Chicken")), 
                tags = listOf(tags[0])
            ),
            FocusBoardItemWithTags(
                item = items[1].copy(title = "App ideas", content = Markdown("1. Habit tracker\n2. AI planner\n3. Note taking")), 
                tags = listOf(tags[1], tags[2])
            ),
            FocusBoardItemWithTags(
                item = items[2].copy(title = "Quick reminder", content = Markdown("Call mom tomorrow at 6 PM")), 
                tags = emptyList()
            )
        )
    }


    private fun randomWord(): String {
        val words = arrayOf("Lorem", "Ipsum", "dolor", "sit", "amet")
        return words[Random().nextInt(words.size)]
    }

    private inline fun <reified T : Enum<T>> randomEnum() = enumValues<T>().random()

    fun shiftDateTime(addHours: Int = 0, addDays: Int = 0): LocalDateTime {
        var datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        datetime = datetime.date.plus(addDays, DateTimeUnit.DAY).atTime(datetime.hour + addHours, datetime.minute)
        return datetime
    }

    fun getTrackedTaskCompletion(
        id: Long = 0,
        activityId: Long = 0,
        date: LocalDateTime = shiftDateTime(nextInt(0, 2))
    ) = TrackedActivityCompletion(id, activityId, date.date, date.time)

    fun getTrackedTaskScore(
        id: Long = 0,
        activityId: Long = 0,
        datetime_scored: LocalDateTime = shiftDateTime(nextInt(0, 2), nextInt(-1, 1)),
        score: Long = nextInt(1, 3).toLong()
    ) = TrackedActivityScore(id, activityId, datetime_scored, score)


    fun getTrackedTaskSession(
        id: Long = 0,
        activityId: Long = 0,
        start: LocalDateTime = shiftDateTime(nextInt(0, 2), nextInt(-1, 1)),
        end: LocalDateTime = start.date.atTime(start.hour + 1, start.minute)
    ) = TrackedActivityTime(id, activityId, start, end)

}