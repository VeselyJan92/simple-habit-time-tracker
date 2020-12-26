package com.imfibit.activitytracker.database

import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

object ReleaseSeeder {

    suspend fun seed(db: AppDatabase){
        activity_the_awesome_project(db)

        activity_exploring(db)

        activity_workout(db)

        activity_point(db)
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


}