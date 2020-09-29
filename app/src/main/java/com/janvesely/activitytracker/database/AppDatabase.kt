package com.janvesely.getitdone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.janvesely.activitytracker.database.Seeder
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivity
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivityCompletion
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivitySession
import com.janvesely.activitytracker.database.dao.tracked_activity.DAOTrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivitySession

import com.janvesely.activitytracker.database.converters.TimeRangeConverter
import com.janvesely.activitytracker.database.converters.LocalDateConverter
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.converters.LocalDateTimeConverter
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.getitdone.database.entities.converters.LocalTimeConverter
import com.janvesely.getitdone.database.entities.converters.TrackedActivityTypeConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Database(
    entities = [
        TrackedActivity::class,
        TrackedActivitySession::class,
        TrackedActivityScore::class,
        TrackedActivityCompletion::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalTimeConverter::class,
    LocalDateConverter::class,
    TimeRangeConverter::class,
    TrackedActivityTypeConverter::class
)

abstract class AppDatabase : RoomDatabase() {

    abstract val activityDAO: DAOTrackedActivity
    abstract val scoreDAO: DAOTrackedActivityScore
    abstract val sessionDAO: DAOTrackedActivitySession
    abstract val completionDAO: DAOTrackedActivityCompletion


    companion object {
        lateinit var db: AppDatabase private set

        val activityRep by lazy { RepositoryTrackedActivity() }


        @Synchronized
        fun init(context: Context){

         /*   db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "database-name"
            ).build()*/

            db = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()


            GlobalScope.launch {
                seed(db, context)
            }

        }

        private suspend fun seed(db: AppDatabase, context: Context){
            val s = Seeder()

            var id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.SCORE,
                name = "Shyby",
                position = 1,
                goal = 2,
                range = TimeRange.WEEKLY
            ))
            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 2, datetime_scored = LocalDateTime.now()))
            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 2))



            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.COMPLETED,
                name = "Posilovna",
                position = 10,
                goal = 1,
                range = TimeRange.DAILY
            ))
            db.completionDAO.insert(s.getTrackedTaskCompletion(activityId = id, date = LocalDate.now()))
            db.completionDAO.insert(s.getTrackedTaskCompletion(activityId = id, date = LocalDate.now().minusDays(1)))

            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.COMPLETED,
                name = "Posilovna 2",
                position = 11,
                goal = 1,
                range = TimeRange.DAILY
            ))




            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.SESSION,
                name = "Prace na pozemku",
                position = 3,
                inSession = LocalDateTime.now(),
                goal = 3600,
                range = TimeRange.MONTHLY
            ))

            val start = LocalTime.of(9, 0)
            val end = LocalTime.of(10, 0)

            db.sessionDAO.insert(s.getTrackedTaskSession(
                activityId = id,
                start = LocalDate.now().atTime(start),
                end = LocalDate.now().atTime(end)
            ))

            db.sessionDAO.insert(s.getTrackedTaskSession(
                activityId = id,
                start = LocalDate.now().minusDays(1).atTime(start),
                end = LocalDate.now().minusDays(1).atTime(end)
            ))


            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.SESSION,
                name = "Zajímavosti",
                position = 4,
                inSession = null,
                goal = 0,
                range = TimeRange.DAILY
            ))


            db.sessionDAO.insert(s.getTrackedTaskSession(
                activityId = id,
                start = LocalDate.now().atTime(start),
                end = LocalDate.now().atTime(end)
            ))

            db.sessionDAO.insert(s.getTrackedTaskSession(
                activityId = id,
                start = LocalDate.now().minusDays(1).atTime(start),
                end = LocalDate.now().minusDays(1).atTime(end)
            ))


            val xx = db.activityDAO.getAll()

        }

    }

}
