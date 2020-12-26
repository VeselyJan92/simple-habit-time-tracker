package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase



val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            UPDATE tracked_activity SET type = 'TIME' WHERE type = 'SESSION'
        """)

        database.execSQL("""
             DROP VIEW IF EXISTS tracked_activity_metricx
        """)

        database.execSQL("""CREATE VIEW `tracked_activity_metric` AS SELECT
            tracked_activity_id,
            datetime_completed as date,
            1 as metric
        FROM tracked_activity_completion
        UNION ALL
        SELECT 
            tracked_activity_id,
            date(time_completed) as date,
            score as metric
        FROM tracked_activity_score
        UNION ALL
        SELECT 
            tracked_activity_id,
            date(time_start) as date,
            strftime('%s',time_end) - strftime('%s', time_start) as metric
        FROM tracked_activity_session""")







    }
}