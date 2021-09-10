package com.imfibit.activitytracker.ui.screens.settings

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.imfibit.activitytracker.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenSettingVM @Inject constructor() : ViewModel(){

    fun exportDB(context: Context, uri: Uri){
        val bytes = context.getDatabasePath(AppDatabase.DB_NAME).readBytes()
        context.contentResolver.openOutputStream(uri)?.write(bytes)
    }

    fun importDB(context: Context, uri: Uri){
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!

        val db = context.getDatabasePath(AppDatabase.DB_NAME)

        context.deleteDatabase(AppDatabase.DB_NAME)

        db.writeBytes(bytes)

        (context as Activity).finishAffinity()
    }
}