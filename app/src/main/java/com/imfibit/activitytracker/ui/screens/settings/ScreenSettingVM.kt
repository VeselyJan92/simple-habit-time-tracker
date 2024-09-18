package com.imfibit.activitytracker.ui.screens.settings

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ScreenSettingVM @Inject constructor(
    val db: AppDatabase
) : BaseViewModel(){

    fun exportDB(context: Context, uri: Uri) = launchIO {
        db.query("PRAGMA wal_checkpoint(FULL)", null).moveToFirst()
        db.query("VACUUM", null).moveToFirst()

        val bytes = context.getDatabasePath(AppDatabase.DB_NAME).readBytes()

        context.contentResolver.openOutputStream(uri)?.apply {
            write(bytes)
            close()
        }
    }

    fun importDB(context: Context, uri: Uri){
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!

        val db = context.getDatabasePath(AppDatabase.DB_NAME)

        context.deleteDatabase(AppDatabase.DB_NAME)

        db.writeBytes(bytes)

        (context as Activity).finishAffinity()
        exitProcess(0);
    }
}