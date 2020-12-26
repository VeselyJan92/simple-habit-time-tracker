package com.imfibit.activitytracker.database

import android.content.Context
import android.telecom.Call
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.imfibit.activitytracker.database.migrations.MIGRATION_2_3
import com.imfibit.activitytracker.database.migrations.migrations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    version = 3,
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
