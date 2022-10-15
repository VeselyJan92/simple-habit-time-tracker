package com.imfibit.activitytracker.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.imfibit.activitytracker.core.services.GlobalWidgetUpdateService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SampleBootReceiver @Inject constructor(
    val service: GlobalWidgetUpdateService
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            service.setAlarmManager()
        }
    }
}