package com.imfibit.activitytracker.core.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imfibit.activitytracker.core.createInvalidationTacker
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import dagger.hilt.android.AndroidEntryPoint

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class UpdateWidgetsOnMidnightReceiver : BroadcastReceiver() {

    @Inject lateinit var updateWidgetsOnMidnightService: GlobalWidgetUpdateService

    override fun onReceive(context: Context?, intent: Intent?)  = runBlocking(Dispatchers.IO){
        Log.e("UpdateWidgetsOnMidnightReceiver", "UPDATE")
        updateWidgetsOnMidnightService.updateWidgets()
    }
}

@AndroidEntryPoint
class RescheduleWidgetUpdates : BroadcastReceiver() {

    @Inject lateinit var updateWidgetsOnMidnightService: GlobalWidgetUpdateService

    override fun onReceive(context: Context?, intent: Intent?)  = runBlocking(Dispatchers.IO){
        updateWidgetsOnMidnightService.setAlarmManager()
    }

}


@Singleton
class GlobalWidgetUpdateService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val overviewWidgetService: OverviewWidgetService
) {
    val TAG = "UpdateWidgetsOnMidnightService"

    val tracker =  createInvalidationTacker(
        TrackedActivity.TABLE,
        TrackedActivityTime.TABLE,
        TrackedActivityCompletion.TABLE,
        TrackedActivityScore.TABLE
    ) {
        Log.e(TAG, "WidgetInvalidationTracker")

        GlobalScope.launch(Dispatchers.IO) {
            updateWidgets()
        }

    }

    fun setAlarmManager(){
        Log.e(TAG, "setAlarmManager")
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, UpdateWidgetsOnMidnightReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 5)
            set(Calendar.SECOND, 0)
        }

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    suspend fun updateWidgets(){
        Log.e(TAG, "updateWidgets")
        overviewWidgetService.updateWidgets()
    }
}