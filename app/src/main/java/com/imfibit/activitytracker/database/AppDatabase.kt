package com.imfibit.activitytracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imfibit.activitytracker.BuildConfig

import com.imfibit.activitytracker.database.converters.TimeRangeConverter
import com.imfibit.activitytracker.database.converters.LocalDateConverter
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.converters.LocalDateTimeConverter
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.database.converters.LocalTimeConverter
import com.imfibit.activitytracker.database.converters.TrackedActivityTypeConverter
import com.imfibit.activitytracker.database.migrations.MIGRATION_1_2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Database(
    entities = [
        TrackedActivity::class,
        TrackedActivityTime::class,
        TrackedActivityScore::class,
        TrackedActivityCompletion::class
    ],
    views = [
        TrackedActivityMetric::class
    ],
    version = 2,
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
    abstract val sessionDAO: DAOTrackedActivityTime
    abstract val completionDAO: DAOTrackedActivityChecked
    abstract val metricDAO: DAOTrackedActivityMetric


    companion object {
        const val DB_NAME ="activity_trcker.db"

        lateinit var db: AppDatabase private set

        val activityRep by lazy { RepositoryTrackedActivity() }


        @Synchronized
        fun init(context: Context){
            when(BuildConfig.BUILD_TYPE){
                "release"->{
                    db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
                "debug" -> {
                    db =  Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                        .fallbackToDestructiveMigration()
                        .build()

                    GlobalScope.launch {
                        seed(db, context)
                    }
                }
                else -> throw IllegalArgumentException()
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

            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 3))
            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 4))
            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 5))
            db.scoreDAO.insert(s.getTrackedTaskScore(activityId = id, score = 6))



            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.CHECKED,
                name = "Posilovna",
                position = 10,
                goal = 1,
                range = TimeRange.DAILY
            ))
            db.completionDAO.insert(s.getTrackedTaskCompletion(activityId = id, date = LocalDate.now()))
            db.completionDAO.insert(s.getTrackedTaskCompletion(activityId = id, date = LocalDate.now().minusDays(1)))

            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.CHECKED,
                name = "Posilovna 2",
                position = 11,
                goal = 1,
                range = TimeRange.DAILY
            ))




            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.TIME,
                name = "Prace na pozemku",
                position = 3,
                inSession = null,
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

            db.sessionDAO.insert(s.getTrackedTaskSession(
                    activityId = id,
                    start = LocalDate.now().minusDays(2).atTime(23, 0),
                    end = LocalDate.now().minusDays(1).atTime(0, 1)
            ))


            id = db.activityDAO.insert(s.getTrackedActivity(
                type = TrackedActivity.Type.TIME,
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



        }

    }

}
