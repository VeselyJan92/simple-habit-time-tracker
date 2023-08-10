package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE `focus_board_items` (`focus_board_item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `position` INTEGER NOT NULL);
        """)

        database.execSQL("""
            CREATE INDEX `focus_board_item_id_pk` ON `focus_board_items` (`focus_board_item_id`);
        """)

        database.execSQL("""
            CREATE TABLE `focus_board_item_tags` (`focus_board_item_tag_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `color` INTEGER NOT NULL, `position` INTEGER NOT NULL);
        """)

        database.execSQL("""
            CREATE INDEX `focus_board_item_tag_id_pk` ON `focus_board_item_tags` (`focus_board_item_tag_id`);
        """)

        database.execSQL("""
            CREATE TABLE `focus_board_item_tag_relation` (`focus_board_item_id` INTEGER NOT NULL, `focus_board_item_tag_id` INTEGER NOT NULL, `primary_tag` INTEGER NOT NULL, PRIMARY KEY(`focus_board_item_id`, `focus_board_item_tag_id`), FOREIGN KEY(`focus_board_item_id`) REFERENCES `focus_board_items`(`focus_board_item_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`focus_board_item_tag_id`) REFERENCES `focus_board_item_tags`(`focus_board_item_tag_id`) ON UPDATE NO ACTION ON DELETE CASCADE );
        """)

        database.execSQL("""
            CREATE INDEX `focus_board_item_id_fk` ON `focus_board_item_tag_relation` (`focus_board_item_id`);
        """)

        database.execSQL("""
            CREATE INDEX `focus_board_item_tag_fk` ON `focus_board_item_tag_relation` (`focus_board_item_tag_id`);
        """)

        database.execSQL("""
            CREATE UNIQUE INDEX `focus_board_item_tag_relation_pk` ON `focus_board_item_tag_relation` (`focus_board_item_tag_id`, `focus_board_item_id`);
        """)
    }
}