package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE `daily_checklist_items` (`daily_checklist_item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `color` INTEGER NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `date_checked` TEXT, `position` INTEGER NOT NULL);
        """)

        database.execSQL("""
            CREATE INDEX `daily_checklist_item_id_pk` ON `daily_checklist_items` (`daily_checklist_item_id`);
        """)

        database.execSQL("""
            CREATE TABLE `daily_checklist_timeline` (`date_completed` TEXT PRIMARY KEY NOT NULL);
        """)

        database.execSQL("""
            CREATE INDEX `date_completed_pk` ON `daily_checklist_timeline` (`date_completed`);
        """)
    }
}

