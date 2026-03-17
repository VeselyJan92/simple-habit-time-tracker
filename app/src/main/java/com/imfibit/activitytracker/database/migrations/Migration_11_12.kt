package com.imfibit.activitytracker.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create the new focus_bundles table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `${FocusBundle.TABLE}` (
                `focus_bundle_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL, 
                `color` INTEGER NOT NULL, 
                `position` INTEGER NOT NULL
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS `focus_bundle_id_pk` ON `${FocusBundle.TABLE}` (`focus_bundle_id`)")
        db.execSQL("INSERT INTO `${FocusBundle.TABLE}` (focus_bundle_id, title, color, position) VALUES (1, 'My Focus', 0, 0)")

        // 2. FocusBoardItem: add bundle_id, dashboard_position and extra metadata columns
        db.execSQL("""
            CREATE TABLE `${FocusBoardItem.TABLE}_new` (
                `focus_board_item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `bundle_id` INTEGER NOT NULL, 
                `title` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `bundle_position` INTEGER NOT NULL, 
                `dashboard_position` INTEGER,
                `is_completed` INTEGER NOT NULL,
                `completed_at` INTEGER,
                `is_pinned` INTEGER NOT NULL,
                `is_starred` INTEGER NOT NULL,
                `updated_at` INTEGER NOT NULL,
                FOREIGN KEY(`bundle_id`) REFERENCES `${FocusBundle.TABLE}`(`focus_bundle_id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """)
        db.execSQL("""
            INSERT INTO `${FocusBoardItem.TABLE}_new` (
                focus_board_item_id, bundle_id, title, content, 
                bundle_position, dashboard_position, 
                is_completed, completed_at, is_pinned, is_starred, updated_at
            )
            SELECT focus_board_item_id, 1, title, content, position, NULL, 0, NULL, 0, 0, 0 
            FROM `${FocusBoardItem.TABLE}`
        """)
        db.execSQL("DROP TABLE `${FocusBoardItem.TABLE}`")
        db.execSQL("ALTER TABLE `${FocusBoardItem.TABLE}_new` RENAME TO `${FocusBoardItem.TABLE}`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `focus_board_item_id_pk` ON `${FocusBoardItem.TABLE}` (`focus_board_item_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `focus_board_item_bundle_id_idx` ON `${FocusBoardItem.TABLE}` (`bundle_id`)")

        // 3. FocusBoardItemTag: add bundle_id and is_task_tag
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `${FocusBoardItemTag.TABLE}_new` (
                `focus_board_item_tag_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `bundle_id` INTEGER NOT NULL, 
                `title` TEXT NOT NULL, 
                `color` INTEGER NOT NULL, 
                `position` INTEGER NOT NULL, 
                `is_checked` INTEGER NOT NULL,
                `is_task_tag` INTEGER NOT NULL,
                FOREIGN KEY(`bundle_id`) REFERENCES `${FocusBundle.TABLE}`(`focus_bundle_id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """)
        db.execSQL("""
            INSERT INTO `${FocusBoardItemTag.TABLE}_new` (focus_board_item_tag_id, bundle_id, title, color, position, is_checked, is_task_tag)
            SELECT focus_board_item_tag_id, 1, title, color, position, is_checked, 0 FROM `${FocusBoardItemTag.TABLE}`
        """)
        db.execSQL("DROP TABLE `${FocusBoardItemTag.TABLE}`")
        db.execSQL("ALTER TABLE `${FocusBoardItemTag.TABLE}_new` RENAME TO `${FocusBoardItemTag.TABLE}`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `focus_board_item_tag_id_pk` ON `${FocusBoardItemTag.TABLE}` (`focus_board_item_tag_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `focus_board_item_tag_bundle_id_idx` ON `${FocusBoardItemTag.TABLE}` (`bundle_id`)")
    }
}
