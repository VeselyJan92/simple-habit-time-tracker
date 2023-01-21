package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE tracked_activity ADD COLUMN challenge_name TEXT DEFAULT '' NOT NULL;""")
        database.execSQL("""ALTER TABLE tracked_activity ADD COLUMN challenge_target INTEGER DEFAULT 0 NOT NULL;""")
        database.execSQL("""ALTER TABLE tracked_activity ADD COLUMN challenge_from TEXT;""")
        database.execSQL("""ALTER TABLE tracked_activity ADD COLUMN challenge_to TEXT;""")
    }
}





















