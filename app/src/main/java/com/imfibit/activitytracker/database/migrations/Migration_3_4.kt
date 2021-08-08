package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE tracked_activity ADD timer INTEGER;""")

        database.execSQL("""
            CREATE TABLE preset_timer(
                preset_timer_id     INTEGER not null primary key autoincrement,
                tracked_activity_id INTEGER not null references tracked_activity(tracked_activity_id) on delete cascade,
                seconds             INTEGER not null,
                position            INTEGER not null
            );
        """.trimIndent())

        database.execSQL("""
            create index index_preset_timer_tracked_activity_id on preset_timer (tracked_activity_id);
      """)

        database.execSQL("""
            create index preset_timer_pk on preset_timer (preset_timer_id);
        """)

    }

}