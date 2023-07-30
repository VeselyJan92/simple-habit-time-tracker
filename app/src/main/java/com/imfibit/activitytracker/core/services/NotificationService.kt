package com.imfibit.activitytracker.core.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun shouldAskForNotification(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            val askForNotificationsToggle = context.dataStore.data.first()[PreferencesKeys.ASK_FOR_NOTIFICATION] ?: true

            askForNotificationsToggle && !isGranted
        }else{
            false
        }
    }

}