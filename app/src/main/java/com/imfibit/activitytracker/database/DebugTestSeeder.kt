package com.imfibit.activitytracker.database

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.imfibit.activitytracker.database.embedable.Markdown
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

        activity_point(db)

        categories(db)

        createFocusBoard(db)

        createDailyChecklist(db)

    }

    private suspend fun createDailyChecklist(db: AppDatabase) {
        DevSeeder.getDailyChecklistTimelineCompletions().forEach {
            db.dailyCheckListTimelineDAO().insert(it)
        }

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "🥛 Morning protein",
            description = "Take 40g of supplements.",
            color = Colors.chooseableColors[8].toArgb()
        ))

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "📅 Plan today",
            description = "Decide what to do today.",
            color = Colors.chooseableColors[12].toArgb()
        ))

        db.dailyCheckListItemsDao().insert(DailyChecklistItem(
            title = "🚫 No sugar",
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
                id = 0, name = "🧠 My project",
                position = 2,
                type = TrackedActivity.Type.TIME,
                inSessionSince = null,
                goal = TrackedActivityGoal(0, TimeRange.WEEKLY),
                challenge = TrackedActivityChallenge("Release MVP", 100 * 3600, today.minus(1, DateTimeUnit.MONTH), today.plus(1, DateTimeUnit.MONTH))
            )
        )


        db.presetTimersDAO().insert(PresetTimer(0,activityId, 60, 0))
        db.presetTimersDAO().insert(PresetTimer(0,activityId, 120, 0))
        db.presetTimersDAO().insert(PresetTimer(0,activityId, 60*30, 0))


        val nowTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Ensure hour is within 0-23 by clamping or using arithmetic
        val startHour = (nowTime.hour - 2).coerceIn(0, 23)
        val endHour = (nowTime.hour - 1).coerceIn(0, 23)

        db.sessionDAO().insert(
            TrackedActivityTime(
                activity_id = activityId,
                id = 0,
                datetime_start = LocalDateTime(nowTime.date, LocalTime(startHour, nowTime.minute)),
                datetime_end = LocalDateTime(nowTime.date, LocalTime(endHour, nowTime.minute))
            )
        )

        repeat(20){
            if (Random.nextBoolean()){
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
            id = 0, name = "🗣️ Learning Spanish",
            position = 1,
            type = TrackedActivity.Type.TIME,
            inSessionSince = now,
            goal = TrackedActivityGoal(30 * 60, TimeRange.DAILY),
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
            id = 0, name = "🏋️ Workout routine",
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
            id = 0, name = "💎 Acquired points",
            position = 4,
            type = TrackedActivity.Type.SCORE,
            inSessionSince = null,
            goal = TrackedActivityGoal(100, TimeRange.WEEKLY),
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
        val categoryId = db.groupDAO().insert(TrackerActivityGroup(0, "💼 Work", 1))

        db.groupDAO().insert(TrackerActivityGroup(0, "🎨 Hobbies", 2))

        val activityId = db.activityDAO().insert(TrackedActivity(
            id = 0,
            name = "🚀 Project management",
            position = 1,
            groupId = categoryId,
            type = TrackedActivity.Type.TIME,
            goal = TrackedActivityGoal(0, TimeRange.DAILY),
            challenge = TrackedActivityChallenge.empty
        ))

        db.activityDAO().insert(TrackedActivity(
            id = 0,
            name = "📅 Meetings",
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

    suspend fun createFocusBoard(db: AppDatabase) {
        val bundle1Id = db.focusBundleDAO().insert(FocusBundle(title = "Personal Life", color = Colors.chooseableColors[0].toArgb().toLong(), position = 0))
        val bundle2Id = db.focusBundleDAO().insert(FocusBundle(title = "Work Projects", color = Colors.chooseableColors[3].toArgb().toLong(), position = 1))
        val bundle3Id = db.focusBundleDAO().insert(FocusBundle(title = "Flat Renovation", color = Colors.chooseableColors[1].toArgb().toLong(), position = 2)) // Translated from Přestavba bytu
        val bundle4Id = db.focusBundleDAO().insert(FocusBundle(title = "Learning & Courses", color = Colors.chooseableColors[7].toArgb().toLong(), position = 3))

        val b1Tag1 = FocusBoardItemTag(bundleId = bundle1Id, name = "Family", color = Colors.chooseableColors[1].toArgb(), position = 1, isTaskTag = false).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }
        val b1Tag2 = FocusBoardItemTag(bundleId = bundle1Id, name = "Errands", color = Colors.chooseableColors[5].toArgb(), position = 2, isTaskTag = true).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }

        val b2Tag1 = FocusBoardItemTag(bundleId = bundle2Id, name = "Ideas", color = Colors.chooseableColors[4].toArgb(), position = 1, isTaskTag = false).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }
        val b2Tag2 = FocusBoardItemTag(bundleId = bundle2Id, name = "Tasks", color = Colors.chooseableColors[0].toArgb(), position = 2, isTaskTag = true).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }

        val b3Tag1 = FocusBoardItemTag(bundleId = bundle3Id, name = "Flat", color = Colors.chooseableColors[10].toArgb(), position = 1, isTaskTag = false).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) } // Translated from byt
        val b3Tag2 = FocusBoardItemTag(bundleId = bundle3Id, name = "Tasks", color = Colors.chooseableColors[4].toArgb(), position = 2, isTaskTag = true).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) } // Translated from úkoly

        val b4Tag1 = FocusBoardItemTag(bundleId = bundle4Id, name = "Notes", color = Colors.chooseableColors[12].toArgb(), position = 1, isTaskTag = false).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }
        val b4Tag2 = FocusBoardItemTag(bundleId = bundle4Id, name = "Assignments", color = Colors.chooseableColors[15].toArgb(), position = 2, isTaskTag = true).let { it.copy(id = db.focusBoardItemTagDAO().insert(it)) }

        // --- Bundle 1: Personal Life ---
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Buy anniversary gift", content = Markdown("Need to find something special. **Must be wrapped!**"), dashboardPosition = 1, isStarred = true), listOf(b1Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Call mom on Sunday", content = Markdown("Ask her how the /new garden/ is coming along."), isCompleted = true, completedAt = System.currentTimeMillis(), isStarred = false, isPinned = true), listOf(b1Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Take out the trash", content = Markdown("")), listOf(b1Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Water the plants", content = Markdown(""), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b1Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Book dentist appointment", content = Markdown("Check for next Tuesday morning.")), listOf(b1Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle1Id, title = "Family dinner plans", content = Markdown("Discuss where to eat for the holidays.")), listOf(b1Tag1) // Non-task tag, non-completed
        )

        // --- Bundle 2: Work Projects ---
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Finish Q3 Report", content = Markdown("Due next Friday. Make sure to double check the **metrics**."), dashboardPosition = 2, isStarred = true), listOf(b2Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Brainstorm app features", content = Markdown("Maybe add a habit tracker? Or a daily journal section?"), isPinned = true), listOf(b2Tag1)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Reply to Sarah's email", content = Markdown("")), listOf(b2Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Approve PR #42", content = Markdown(""), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b2Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Update Jira tickets", content = Markdown(""), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b2Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle2Id, title = "Research new DB tech", content = Markdown("Look into Room updates and migrations.")), listOf(b2Tag1) // Non-task tag
        )

        // --- Bundle 3: Flat Renovation ---
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Kitchen and cabinets", content = Markdown(""), dashboardPosition = 3, isStarred = true), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Small angle valve behind washer", content = Markdown(""), isPinned = true), listOf(b3Tag1) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Felt pads for furniture legs", content = Markdown("")), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Lights", content = Markdown(""), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Sink", content = Markdown("")), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Washing machine", content = Markdown(""), isPinned = true), listOf(b3Tag1) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Trash bin system", content = Markdown("")), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Blinds", content = Markdown("")), listOf(b3Tag1) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Table", content = Markdown("")), listOf(b3Tag2) // Translated
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle3Id, title = "Chairs", content = Markdown("")), listOf(b3Tag2) // Translated
        )

        // --- Bundle 4: Learning & Courses ---
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Read Chapter 5 of Clean Code", content = Markdown("Focus on the section about **formatting** and **comments**."), isPinned = true), listOf(b4Tag1) // Non-task tag
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Finish Compose tutorial", content = Markdown("Complete the codelab on _state management_."), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b4Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Buy new notebook", content = Markdown(""), isCompleted = true, completedAt = System.currentTimeMillis()), listOf(b4Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Watch Kotlin Conf video", content = Markdown("")), listOf(b4Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Practice LeetCode", content = Markdown("Do at least 2 easy problems today.")), listOf(b4Tag2)
        )
        focusBoardRepository.insertFocusItemWithTags(
            FocusBoardItem(bundleId = bundle4Id, title = "Architecture Patterns", content = Markdown("Need to deeply understand MVVM and MVI.")), listOf(b4Tag1) // Non-task tag
        )
    }

}