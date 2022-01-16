package com.imfibit.activitytracker.database

import android.util.Log
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

object ReleaseSeeder {

    suspend fun seed(db: AppDatabase){

        Log.e("SEED", "SEED")

        activity_the_awesome_project(db)

        activity_exploring(db)

        activity_workout(db)

        activity_point(db)

        categories(db)


       /* var activityId: Long = 0

        activityId = db.activityDAO.insert(
            TrackedActivity(
                id = 0, name = "TEST",
                position = 1,
                type = TrackedActivity.Type.TIME,
                inSessionSince = null,
                goal = TrackedActivityGoal(0, TimeRange.WEEKLY)
            )
        )
        var x = LocalDate.of(2021, 12, 27)


        repeat(7){
            db.sessionDAO.insert(TrackedActivityTime(0, activityId, x.atTime(12, 0), x.atTime(13, 0)))
            x = x.plusDays(1)
        }*/






    }


    suspend fun activity_the_awesome_project(db: AppDatabase) {
        var activityId: Long = 0

        activityId = db.activityDAO.insert(
            TrackedActivity(
                id = 0, name = "The awesome project",
                position = 1,
                type = TrackedActivity.Type.TIME,
                inSessionSince = null,
                goal = TrackedActivityGoal(0, TimeRange.WEEKLY)
            )
        )


        db.presetTimersDAO.insert(PresetTimer(0,activityId, 60, 0))
        db.presetTimersDAO.insert(PresetTimer(0,activityId, 120, 0))
        db.presetTimersDAO.insert(PresetTimer(0,activityId, 60*30, 0))


        db.sessionDAO.insert(
            TrackedActivityTime(
                activity_id = activityId,
                id = 0,
                datetime_start = LocalDateTime.now().minusHours(2),
                datetime_end = LocalDateTime.now().minusHours(1)
            )
        )

        repeat(20){
            if (Random.nextBoolean()){
                db.sessionDAO.insert(TrackedActivityTime(
                        activity_id = activityId,
                        id = 0,
                        datetime_start = LocalDateTime.now().minusHours(2).minusDays(it.toLong() + 1),
                        datetime_end = LocalDateTime.now().minusHours(1).minusDays(it.toLong() + 1)
                ))
            }

        }

    }

    suspend fun activity_exploring(db: AppDatabase) {
        var activityId: Long = 0

        activityId = db.activityDAO.insert(TrackedActivity(
            id = 0, name = "Exploring new App",
            position = 1,
            type = TrackedActivity.Type.TIME,
            inSessionSince = LocalDateTime.now(),
            goal = TrackedActivityGoal(0, TimeRange.DAILY)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(2),
            datetime_end = LocalDateTime.now().minusHours(1)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(4),
            datetime_end = LocalDateTime.now().minusHours(3)
        ))


        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(4).minusDays(1),
            datetime_end = LocalDateTime.now().minusHours(3).minusDays(1)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(4).minusDays(3),
            datetime_end = LocalDateTime.now().minusHours(3).minusDays(3)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(4).minusDays(10),
            datetime_end = LocalDateTime.now().minusHours(3).minusDays(10)
        ))



    }

    suspend fun activity_workout(db: AppDatabase) {
        var activityId: Long = 0

        activityId = db.activityDAO.insert(TrackedActivity(
            id = 0, name = "Workout routine",
            position = 1,
            type = TrackedActivity.Type.CHECKED,
            inSessionSince = null,
            goal = TrackedActivityGoal(3, TimeRange.WEEKLY)
        ))


        db.completionDAO.insert(TrackedActivityCompletion(
            id = 0,
            activity_id = activityId,
            date_completed = LocalDate.now(),
            time_completed = LocalTime.now(),
        ))

        repeat(20){
            if (Random.nextBoolean()){
                db.completionDAO.insert(TrackedActivityCompletion(
                    id = 0,
                    activity_id = activityId,
                    date_completed = LocalDate.now().minusDays(it + 1L),
                    time_completed = LocalTime.now(),
                ))
            }
        }

    }

    suspend fun activity_point(db: AppDatabase) {
        var activityId: Long = 0

        activityId = db.activityDAO.insert(TrackedActivity(
            id = 0, name = "Acquired points",
            position = 1,
            type = TrackedActivity.Type.SCORE,
            inSessionSince = null,
            goal = TrackedActivityGoal(3, TimeRange.WEEKLY)
        ))

        db.scoreDAO.insert(TrackedActivityScore(
            id = 0,
            activity_id = activityId,
            datetime_completed = LocalDateTime.now(),
            score = 42
        ))

        repeat(20){
            if (Random.nextBoolean()){
                db.scoreDAO.insert(TrackedActivityScore(
                    id = 0,
                    activity_id = activityId,
                    datetime_completed = LocalDateTime.now().minusDays(it + 1L ),
                    score = Random.nextLong(10, 20)
                ))
            }
        }
    }

    suspend fun categories(db: AppDatabase){
        val categoryId = db.groupDAO.insert(TrackerActivityGroup(0, "Work", 1))

        db.groupDAO.insert(TrackerActivityGroup(0, "Hobbies", 2))

        db.groupDAO.insert(TrackerActivityGroup(0, "Other", 2))

        db.groupDAO.insert(TrackerActivityGroup(0, "Workout", 2))

        db.groupDAO.insert(TrackerActivityGroup(0, "Gym", 2))

        val activityId = db.activityDAO.insert(TrackedActivity(
            id = 0,
            name = "Project management",
            position = 1,
            groupId = categoryId,
            type = TrackedActivity.Type.TIME,
            goal = TrackedActivityGoal(0, TimeRange.DAILY)
        ))

        db.activityDAO.insert(TrackedActivity(
            id = 0,
            name = "Project management 2",
            position = 2,
            groupId = categoryId,
            type = TrackedActivity.Type.TIME,
            goal = TrackedActivityGoal(0, TimeRange.DAILY)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(2).minusDays(2),
            datetime_end = LocalDateTime.now().minusHours(1).minusDays(2)
        ))

        db.sessionDAO.insert(TrackedActivityTime(
            activity_id = activityId,
            id = 0,
            datetime_start = LocalDateTime.now().minusHours(4).minusDays(2),
            datetime_end = LocalDateTime.now().minusHours(3).minusDays(2)
        ))
    }


}