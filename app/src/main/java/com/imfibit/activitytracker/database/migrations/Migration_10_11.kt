package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE focus_board_item_tags ADD COLUMN is_checked INTEGER DEFAULT TRUE NOT NULL")
    }
}

