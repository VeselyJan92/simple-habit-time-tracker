package com.imfibit.activitytracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imfibit.activitytracker.BuildConfig
import com.imfibit.activitytracker.database.converters.LocalDateConverter
import com.imfibit.activitytracker.database.converters.LocalDateTimeConverter
import com.imfibit.activitytracker.database.converters.LocalTimeConverter
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.migrations.migrations
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


@Database(
    entities = [
        TrackedActivity::class,
        TrackedActivityTime::class,
        TrackedActivityScore::class,
        TrackedActivityCompletion::class,
        PresetTimer::class
    ],
    views = [
        TrackedActivityMetric::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalTimeConverter::class,
    LocalDateConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract val activityDAO: DAOTrackedActivity
    abstract val scoreDAO: DAOTrackedActivityScore
    abstract val sessionDAO: DAOTrackedActivityTime
    abstract val completionDAO: DAOTrackedActivityChecked
    abstract val metricDAO: DAOTrackedActivityMetric
    abstract val presetTimersDAO: DAOPresetTimers


    companion object {
        const val DB_NAME ="activity_tracker.db"

        lateinit var db: AppDatabase private set

        val activityRep by lazy { RepositoryTrackedActivity() }

        @Synchronized
        fun init(context: Context){
            when(BuildConfig.BUILD_TYPE){
                "release"->{
                    val dbExists = context.getDatabasePath(DB_NAME).exists()

                    db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .addMigrations(*migrations)
                        .build()

                    if (!dbExists)
                        runBlocking(Dispatchers.IO) { ReleaseSeeder.seed(db) }
                }
                "debug" -> {
                    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                        .build()

                        runBlocking(Dispatchers.IO) { ReleaseSeeder.seed(db) }
                }
                "personal" ->{
                    db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .addMigrations(*migrations)
                        .build()
                }
                else -> throw IllegalArgumentException()
            }

        }
    }

}
