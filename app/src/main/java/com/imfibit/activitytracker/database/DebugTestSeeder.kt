package com.imfibit.activitytracker.database

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import com.imfibit.activitytracker.ui.components.Colors
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random

object DebugTestSeeder {

    lateinit var focusBoardRepository: RepositoryFocusBoard

    suspend fun seed(db: AppDatabase){

        Log.e("SEED", "SEED")

        focusBoardRepository = RepositoryFocusBoard(db)

        activity_workout(db)

        activity_my_project(db)

        activity_learning_spanish(db)



        categories(db)

        createFocusBoard(db)


        createDailyChecklist(db)

    }

    private suspend fun createDailyChecklist(db: AppDatabase) {
        DevSeeder.getDailyChecklistTimelineCompletions().forEach {
            db.dailyCheckListTimelineDAO().insert(it)
        }

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "Morning protein",
            description = "Take 40g of supplements.",
            color = Colors.chooseableColors[8].toArgb()
        ))

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "Plan today",
            description = "Decide what to do today.",
            color = Colors.chooseableColors[12].toArgb()
        ))

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "No sugar",
            description = "I didn't drink any sugary drinks.",
            color = Colors.chooseableColors[5].toArgb()
        ))

    }


    suspend fun activity_my_project(db: AppDatabase) {
        var activityId: Long = 0

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date

        activityId = db.activityDAO().insert(
            TrackedActivity(
                id = 0, name = "My project",
                position = 2,
                type = TrackedActivity.Type.TIME,
                inSessionSince = null,
                goal = TrackedActivityGoal(0, TimeRange.WEEKLY),
                challenge = TrackedActivityChallenge("Research", 40 * 3600, today.minus(1, DateTimeUnit.MONTH), today.plus(1, DateTimeUnit.MONTH))
            )
        )


        db.presetTimersDAO().insert(PresetTimer(0,activityId, 60, 0))
        db.presetTimersDAO().insert(PresetTimer(0,activityId, 120, 0))
        db.presetTimersDAO().insert(PresetTimer(0,activityId, 60*30, 0))


        val nowTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        db.sessionDAO().insert(
            TrackedActivityTime(
                activity_id = activityId,
                id = 0,
                datetime_start = nowTime.let { LocalDateTime(it.date, LocalTime(it.hour - 2, it.minute)) },
                datetime_end = nowTime.let { LocalDateTime(it.date, LocalTime(it.hour - 1, it.minute)) }
            )
        )

        repeat(20){
            if (Random.nextBoolean()){
                // This logic needs adjustment because kotlinx.datetime arithmetic is a bit different.
                // Assuming simple subtraction of hours/days for mock data.
                // We'll construct new date times by shifting.
                
                // Original: LocalDateTime.now().minusHours(2).minusDays(it.toLong() + 1)
                // kotlinx:
                
                val start = Clock.System.now().minus(2, DateTimeUnit.HOUR).minus(it + 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
                val end = Clock.System.now().minus(1, DateTimeUnit.HOUR).minus(it + 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())

                db.sessionDAO().insert(TrackedActivityTime(
                        activity_id = activityId,
                        id = 0,
                        datetime_start = start,
                        datetime_end = end
                ))
            }

        }

    }

    suspend fun activity_learning_spanish(db: AppDatabase) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        val activityId = db.activityDAO().insert(TrackedActivity(
            id = 0, name = "Learning Spanish",
            position = 1,
            type = TrackedActivity.Type.TIME,
            inSessionSince = now,
            goal = TrackedActivityGoal(0, TimeRange.DAILY),
            challenge = TrackedActivityChallenge.empty
        ))
        
        val nowInstant = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(2, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(1, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(4, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(3, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))


        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(1, DateTimeUnit.DAY, tz).minus(4, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(1, DateTimeUnit.DAY, tz).minus(3, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(3, DateTimeUnit.DAY, tz).minus(4, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(3, DateTimeUnit.DAY, tz).minus(3, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(10, DateTimeUnit.DAY, tz).minus(4, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(10, DateTimeUnit.DAY, tz).minus(3, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))
    }

    suspend fun activity_workout(db: AppDatabase) {
        val activityId = db.activityDAO().insert(TrackedActivity(
            id = 0, name = "Workout routine",
            position = 1,
            type = TrackedActivity.Type.CHECKED,
            inSessionSince = null,
            goal = TrackedActivityGoal(3, TimeRange.WEEKLY),
            challenge = TrackedActivityChallenge.empty
        ))

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        db.completionDAO().insert(TrackedActivityCompletion(
            id = 0,
            activity_id = activityId,
            date_completed = LocalDate(2022, 1, 31),
            time_completed = now.time,
        ))

        db.completionDAO().insert(TrackedActivityCompletion(
            id = 0,
            activity_id = activityId,
            date_completed = LocalDate(2022, 2, 1),
            time_completed = now.time,
        ))
    }

    suspend fun activity_point(db: AppDatabase) {
        var activityId: Long = 0

        activityId = db.activityDAO().insert(TrackedActivity(
            id = 0, name = "Acquired points",
            position = 1,
            type = TrackedActivity.Type.SCORE,
            inSessionSince = null,
            goal = TrackedActivityGoal(3, TimeRange.WEEKLY),
            challenge = TrackedActivityChallenge.empty
        ))
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        db.scoreDAO().insert(TrackedActivityScore(
            id = 0,
            activity_id = activityId,
            datetime_completed = now,
            score = 42
        ))

        repeat(20){
            if (Random.nextBoolean()){
                val date = Clock.System.now().minus(it + 1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault())
                db.scoreDAO().insert(TrackedActivityScore(
                    id = 0,
                    activity_id = activityId,
                    datetime_completed = date,
                    score = Random.nextLong(10, 20)
                ))
            }
        }
    }

    suspend fun categories(db: AppDatabase){
        val categoryId = db.groupDAO().insert(TrackerActivityGroup(0, "Work", 1))

        db.groupDAO().insert(TrackerActivityGroup(0, "Hobbies", 2))

        val activityId = db.activityDAO().insert(TrackedActivity(
            id = 0,
            name = "Project management",
            position = 1,
            groupId = categoryId,
            type = TrackedActivity.Type.TIME,
            goal = TrackedActivityGoal(0, TimeRange.DAILY),
            challenge = TrackedActivityChallenge.empty
        ))

        db.activityDAO().insert(TrackedActivity(
            id = 0,
            name = "Project management 2",
            position = 2,
            groupId = categoryId,
            type = TrackedActivity.Type.TIME,
            goal = TrackedActivityGoal(0, TimeRange.DAILY),
            challenge = TrackedActivityChallenge.empty
        ))
        
        val nowInstant = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(2, DateTimeUnit.DAY, tz).minus(2, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(2, DateTimeUnit.DAY, tz).minus(1, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))

        db.sessionDAO().insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = nowInstant.minus(2, DateTimeUnit.DAY, tz).minus(4, DateTimeUnit.HOUR, tz).toLocalDateTime(tz),
            datetime_end = nowInstant.minus(2, DateTimeUnit.DAY, tz).minus(3, DateTimeUnit.HOUR, tz).toLocalDateTime(tz)
        ))
    }


    suspend fun createFocusBoard(db: AppDatabase){

        val habitTag = FocusBoardItemTag(0, "Habits", Colors.chooseableColors[3].toArgb(), 1).let {
            it.copy(id = db.focusBoardItemTagDAO().insert(it))
        }

        val focusTag = FocusBoardItemTag(0, "Focus", Colors.chooseableColors[7].toArgb(), 1).let {
                it.copy(id = db.focusBoardItemTagDAO().insert(it))
        }

        val sideGoalsTag = FocusBoardItemTag(0, "Side goals", Colors.chooseableColors[9].toArgb(), 1).let {
                it.copy(id = db.focusBoardItemTagDAO().insert(it))
        }

        val work = FocusBoardItemTag(0, "Work", Colors.chooseableColors[13].toArgb(), 1).let {
            it.copy(id = db.focusBoardItemTagDAO().insert(it))
        }

        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(title = "Working out"),
            listOf(habitTag)
        )

        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(title = "Learning spanish"),
            listOf(habitTag)
        )

        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(
                title = "Business and trends research",
                content = "Google doc of business research: \n" + "• Trends, industry, research" + "\n" + "• Technology" + "\n" + "• Understanding business models"
            ),
            listOf(focusTag)
        )

        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(
                title = "My book list",
                content = "Atomic habits: \n" + "• The 7 Habits of Highly Effective People" + "\n" + "• The Richest Man in Babylon",
            ),
            listOf(focusTag, work)
        )

        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(
                title = "Sell old stuff",
                content = "I need to sell things that I no longer need that just take up space",
            ),
            listOf(sideGoalsTag)
        )

    }

}