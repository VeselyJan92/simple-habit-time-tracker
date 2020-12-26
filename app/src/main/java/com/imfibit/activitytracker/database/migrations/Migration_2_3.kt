package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""DROP VIEW tracked_activity_metric""")

        session(database)
        score(database)
        completion(database)

        database.execSQL("""CREATE VIEW `tracked_activity_metric` AS SELECT
            tracked_activity_id,
            date_completed as date,
            1 as metric
        FROM tracked_activity_completion
        UNION ALL
        SELECT
            tracked_activity_id,
            date(datetime_completed) as date,
            score as metric
        FROM tracked_activity_score
        UNION ALL
        SELECT
            tracked_activity_id,
            date(datetime_start) as date,
            strftime('%s',datetime_end) - strftime('%s', datetime_start) as metric
        FROM tracked_activity_session
        """)
    }


    fun session(database: SupportSQLiteDatabase){
        database.execSQL("""
            create table tracked_activity_session_dg_tmp(
                tracked_activity_session_id INTEGER not null primary key autoincrement,
                tracked_activity_id INTEGER not null references tracked_activity (tracked_activity_id) on delete cascade,
                datetime_start TEXT not null,
                datetime_end TEXT not null
            );
        """)

        database.execSQL("""
            insert into tracked_activity_session_dg_tmp(tracked_activity_session_id, tracked_activity_id, datetime_start, datetime_end)
            select tracked_activity_session_id, tracked_activity_id, time_start, time_end from tracked_activity_session;
        """)

        database.execSQL("""
            drop table tracked_activity_session;

        """)

        database.execSQL("""
            alter table tracked_activity_session_dg_tmp rename to tracked_activity_session;
        """)

        database.execSQL("""
            create index index_tracked_activity_session_tracked_activity_id
            on tracked_activity_session (tracked_activity_id);
        """)

        database.execSQL("""
            create index index_tracked_activity_session_tracked_activity_session_id
            on tracked_activity_session (tracked_activity_session_id);
        """)







    }

    fun score(database: SupportSQLiteDatabase){

        database.execSQL("""
            create table tracked_activity_score_dg_tmp(
                tracked_activity_score_id INTEGER not null primary key autoincrement,
                tracked_activity_id INTEGER not null references tracked_activity (tracked_activity_id) on delete cascade,
                datetime_completed TEXT not null,
                score INTEGER not null
            );
        """)

        database.execSQL("""
            insert into tracked_activity_score_dg_tmp(tracked_activity_score_id, tracked_activity_id, datetime_completed, score) select tracked_activity_score_id, tracked_activity_id, time_completed, score from tracked_activity_score;

        """)

        database.execSQL("""
            drop table tracked_activity_score;

        """)

        database.execSQL("""
            alter table tracked_activity_score_dg_tmp rename to tracked_activity_score;

        """)


        database.execSQL("""
            create index index_tracked_activity_score_tracked_activity_id
            on tracked_activity_score (tracked_activity_id);
        """)


        database.execSQL("""
            create index index_tracked_activity_score_tracked_activity_score_id
            on tracked_activity_score (tracked_activity_score_id);
        """)

    }

    fun completion(database: SupportSQLiteDatabase){
        database.execSQL("""
            create table tracked_activity_completion_dg_tmp(
                tracked_activity_completion_id INTEGER not null primary key autoincrement,
                tracked_activity_id INTEGER not null references tracked_activity (tracked_activity_id) on delete cascade,
                date_completed TEXT not null,
                time_completed TEXT not null
            );
        """)

        database.execSQL("""
            insert into tracked_activity_completion_dg_tmp(tracked_activity_completion_id, tracked_activity_id, date_completed, time_completed)
            select tracked_activity_completion_id, tracked_activity_id, date_completed, '12:00:00.000' from tracked_activity_completion;
        """)

        database.execSQL("""
            drop table tracked_activity_completion;
        """)


        database.execSQL("""
            alter table tracked_activity_completion_dg_tmp rename to tracked_activity_completion;
        """)

        database.execSQL("""
            create unique index index_tracked_activity_completion_date_completed_tracked_activity_id
            on tracked_activity_completion (date_completed, tracked_activity_id);
        """)

        database.execSQL("""
        create index index_tracked_activity_completion_tracked_activity_completion_id
        on tracked_activity_completion (tracked_activity_completion_id);
        """)

        database.execSQL("""
            create index index_tracked_activity_completion_tracked_activity_id
            on tracked_activity_completion (tracked_activity_id);
        """)
    }
}