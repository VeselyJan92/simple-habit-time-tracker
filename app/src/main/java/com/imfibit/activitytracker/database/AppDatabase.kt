package com.imfibit.activitytracker.database

import android.content.Context
import android.util.Log
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.init(context)
    }

    @Provides
    @Singleton
    fun provideRepositoryTrackedActivity(db: AppDatabase): RepositoryTrackedActivity {
        return RepositoryTrackedActivity(db)
    }
}


@Database(
    entities = [
        TrackedActivity::class,
        TrackedActivityTime::class,
        TrackedActivityScore::class,
        TrackedActivityCompletion::class,
        PresetTimer::class,
        TrackerActivityGroup::class
    ],
    views = [
        TrackedActivityMetric::class
    ],
    version = 6,
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
    abstract val groupDAO: DAOActivityGroup

    companion object {
        const val DB_NAME ="activity_tracker.db"

        fun init(context: Context): AppDatabase = when(BuildConfig.BUILD_TYPE){
            "release"->{
                val dbExists = context.getDatabasePath(DB_NAME).exists()

                val db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    .addMigrations(*migrations)
                    .build()

                if (!dbExists)
                    runBlocking(Dispatchers.IO) { ReleaseSeeder.seed(db) }

                db
            }
            "debug" -> {
                context.deleteDatabase(DB_NAME)


                val db = Room
                    .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    //.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    //.createFromAsset("activity_tracker20-10-22.db")
                    .addMigrations(*migrations)
                    .build()

                runBlocking(Dispatchers.IO) { ReleaseSeeder.seed(db) }

                db
            }
            else -> throw IllegalArgumentException()
        }

    }

}
