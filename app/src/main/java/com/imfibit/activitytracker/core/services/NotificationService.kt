package com.imfibit.activitytracker.core.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.imfibit.activitytracker.core.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettings: AppSettings
) {

    suspend fun shouldAskForNotification(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            val askForNotificationsToggle = appSettings.getShouldShowNotificationsPopup() ?: true

            askForNotificationsToggle && !isGranted
        }else{
            false
        }
    }

}