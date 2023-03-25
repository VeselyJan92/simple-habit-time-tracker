package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            create table tracked_activity_dg_tmp(
                tracked_activity_id INTEGER not null primary key autoincrement,
                name                TEXT    not null,
                position            INTEGER not null,
                group_position      INTEGER not null,
                type                TEXT    not null,
                in_session_since    TEXT,
                timer               INTEGER,
                goal_value          INTEGER not null,
                goal_range          TEXT    not null,
                activity_group_id   INTEGER
                    constraint tracked_activity_tracked_activity_group_activity_group_id_fk
                        references tracked_activity_group(activity_group_id)
                        on delete cascade,
                challenge_name      TEXT    not null,
                challenge_target    INTEGER not null,
                challenge_from      TEXT    not null,
                challenge_to        TEXT    not null
               );
        """)

            database.execSQL("""
            insert into tracked_activity_dg_tmp(
                tracked_activity_id,
                name, position,
                group_position,
                type, in_session_since,
                timer,
                goal_value,
                goal_range,
                challenge_name,
                challenge_target,
                challenge_from,
                challenge_to
            ) select
              tracked_activity_id,
              name,
              position,
              0 as group_position,
              type,
              in_session_since,
              timer,
              goal_value,
              goal_range,
              challenge_name,
              challenge_target,
              IFNULL(challenge_from, '2023-02-25') as challenge_from,
              IFNULL(challenge_to, '2023-02-25') as challenge_to
           from tracked_activity;
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

            database.execSQL("""
            create index tracked_activity_group_fk on tracked_activity (activity_group_id);
        """)
    }

}





















