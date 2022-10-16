package com.imfibit.activitytracker.core.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.imfibit.activitytracker.core.createInvalidationTacker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import dagger.hilt.android.AndroidEntryPoint

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class UpdateWidgetsOnMidnightReceiver : BroadcastReceiver() {

    @Inject lateinit var updateWidgetsOnMidnightService: WidgetUpdateService

    override fun onReceive(context: Context?, intent: Intent?)  = runBlocking(Dispatchers.IO){
        Log.e("UpdateWidgetsOnMidnightReceiver", "UPDATE")
        updateWidgetsOnMidnightService.updateWidgets()
    }
}

/**
 * Reschedule alarms when
 * <action android:name="android.intent.action.DATE_CHANGED"/>
 * <action android:name="android.intent.action.TIME_SET"/>
 * <action android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
 */
@AndroidEntryPoint
class RescheduleWidgetUpdates : BroadcastReceiver() {

    @Inject lateinit var widgetUpdateService: WidgetUpdateService

    override fun onReceive(context: Context?, intent: Intent?)  = runBlocking(Dispatchers.IO){
        widgetUpdateService.setMidnightUpdate()
    }
}

@Singleton
class WidgetUpdateService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val overviewWidgetService: OverviewWidgetService
) {
    val TAG = "UpdateWidgetsOnMidnightService"
    private val REQUEST_CODE = 0

    val tracker =  createInvalidationTacker(
        TrackedActivity.TABLE,
        TrackedActivityTime.TABLE,
        TrackedActivityCompletion.TABLE,
        TrackedActivityScore.TABLE
    ) {
        Log.e(TAG, "WidgetInvalidationTracker")

        MainScope().launch(Dispatchers.IO) {
            updateWidgets()
        }
    }

    fun setLiveUpdates(){
        database.invalidationTracker.addObserver(tracker)
    }

    fun setMidnightUpdate(){
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, UpdateWidgetsOnMidnightReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 1);
        }

        Log.e(TAG, "setAlarmManager: ${LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("ECT"))}")

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC,
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