package com.imfibit.activitytracker.ui.screens.settings

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.AndroidViewModel
import com.imfibit.activitytracker.core.registerForActivityResult
import com.imfibit.activitytracker.database.AppDatabase
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    fun export() {
        val application = getApplication<Application>()

        val dbFile = File(application.getDatabasePath(AppDatabase.DB_NAME).absolutePath)

        if (dbFile.exists()){
            val name = "db-export-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".db"

            val resolver = application.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null){
                resolver.openOutputStream(uri).use { outputStream ->
                    dbFile.inputStream().copyTo(outputStream!!)
                }
            }

        }

    }



    fun import(uri: Uri) {
        val application = getApplication<Application>()

        val dbFile = File(application.getDatabasePath(AppDatabase.DB_NAME).absolutePath)
        if (dbFile.delete())
            Log.e("DELETED", "DELETED")


       // dbFile.createNewFile()

       // dbFile.writeBytes(application.contentResolver.openInputStream(uri)!!.readBytes())


    }

}
