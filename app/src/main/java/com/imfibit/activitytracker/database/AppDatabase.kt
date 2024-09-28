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
import com.imfibit.activitytracker.database.dao.DAODailyChecklistItem
import com.imfibit.activitytracker.database.dao.DAODailyChecklistTimeline
import com.imfibit.activitytracker.database.dao.DAOFocusBoardItem
import com.imfibit.activitytracker.database.dao.DAOFocusBoardItemTagRelation
import com.imfibit.activitytracker.database.dao.DAOFocusBoardItemTags
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOActivityGroup
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOPresetTimers
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivity
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityChecked
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityMetric
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityScore
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityTime
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBoardItemTagRelation
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityMetric
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
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
        TrackerActivityGroup::class,
        FocusBoardItem::class,
        FocusBoardItemTag::class,
        FocusBoardItemTagRelation::class,
        DailyChecklistItem::class,
        DailyChecklistTimelineItem::class,
    ],
    views = [
        TrackedActivityMetric::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalTimeConverter::class,
    LocalDateConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDAO(): DAOTrackedActivity
    abstract fun scoreDAO(): DAOTrackedActivityScore
    abstract fun sessionDAO(): DAOTrackedActivityTime
    abstract fun completionDAO(): DAOTrackedActivityChecked
    abstract fun metricDAO(): DAOTrackedActivityMetric
    abstract fun presetTimersDAO(): DAOPresetTimers
    abstract fun groupDAO(): DAOActivityGroup
    abstract fun dailyCheckListTimelineDAO(): DAODailyChecklistTimeline
    abstract fun dailyCheckListItemsDao(): DAODailyChecklistItem

    abstract fun focusBoardItemDAO(): DAOFocusBoardItem
    abstract fun focusBoardItemTagDAO(): DAOFocusBoardItemTags
    abstract fun focusBoardItemTagRelationDAO(): DAOFocusBoardItemTagRelation

    companion object {
        const val DB_NAME ="activity_tracker.db"

        fun init(context: Context): AppDatabase = when(BuildConfig.BUILD_TYPE){
            "release"->{
                val db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    .addMigrations(*migrations)
                    .build()

                db
            }
            "debug", "stage" -> {
               // context.deleteDatabase(DB_NAME)

                val db = Room
                    //.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    .databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    //.createFromAsset("test.db")
                    .addMigrations(*migrations)
                    .build()

                //runBlocking(Dispatchers.IO) { DebugTestSeeder.seed(db) }

                db
            }
            else -> throw IllegalArgumentException()
        }

    }

}
