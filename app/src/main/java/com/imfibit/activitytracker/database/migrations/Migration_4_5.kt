package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            create table tracked_activity_group(
                activity_group_id INTEGER not null primary key autoincrement,
                name              TEXT    not null,
                position          INTEGER not null
            );
        """)

        database.execSQL("""
            create index activity_group_pk on tracked_activity_group (activity_group_id);
        """)

        database.execSQL("""
            create table tracked_activity_dg_tmp(
                tracked_activity_id INTEGER not null primary key autoincrement,
                name TEXT not null,
                position INTEGER not null,
                group_position INTEGER not null, 
                type TEXT not null,
                in_session_since TEXT,
                timer INTEGER,
                goal_value INTEGER not null,
                goal_range TEXT not null,
                activity_group_id INTEGER constraint tracked_activity_tracked_activity_group_activity_group_id_fk references tracked_activity_group (activity_group_id)
                );
        """)

        database.execSQL("""
            insert into tracked_activity_dg_tmp(tracked_activity_id, name, position, group_position, type, in_session_since, timer, goal_value, goal_range) select tracked_activity_id, name, position, 0 as group_position, type, in_session_since, timer, goal_value, goal_range from tracked_activity;
        """)

        database.execSQL("""
            drop table tracked_activity;
        """)

        database.execSQL("""
            alter table tracked_activity_dg_tmp rename to tracked_activity;
        """)

        database.execSQL("""
            create index tracked_activity_pk on tracked_activity (tracked_activity_id);
        """)
    }
}





















