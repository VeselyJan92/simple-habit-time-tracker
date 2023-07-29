package com.imfibit.activitytracker.core.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.core.receivers.ActivityTimerCompletedReceiver
import com.imfibit.activitytracker.core.services.activity.TimeActivityService
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject


class TrackTimeService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
    private val haptics: UserHapticsService,
    @ApplicationContext private val context: Context,
    private val sessionService: TimeActivityService
){

    suspend fun startSession(activity: TrackedActivity, start: LocalDateTime = LocalDateTime.now()) {
        repository.activityDAO.update(activity.apply { inSessionSince = start })

        haptics.activityFeedback()

        NotificationLiveSession.show(context, activity)
    }

    suspend fun commitSession(activity: TrackedActivity){
        if (activity.inSessionSince != null){
            sessionService.insertSession(TrackedActivityTime(0, activity.id, activity.inSessionSince!!, LocalDateTime.now()))
            repository.activityDAO.update(activity.apply { activity.inSessionSince = null });
        }else {
            FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException("Committing already committed session"))
        }

        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)

        cancelTimer(activity)
    }

    suspend fun cancelSession(activity: TrackedActivity) {
        cancelTimer(activity)

        repository.activityDAO.update(activity.apply { activity.inSessionSince = null })

        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)
    }

    suspend fun startWithTimer(activity: TrackedActivity, timer: PresetTimer){
        startSession(activity)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = System.currentTimeMillis() + timer.seconds * 1000


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getTimerIntent(activity)!!)
        }
    }

    suspend fun updateSession(activity: TrackedActivity, start: LocalDateTime){
        cancelTimer(activity)

        activity.inSessionSince = start

        repository.activityDAO.update(activity)

        NotificationLiveSession.show(context, activity)
        NotificationTimerOver.remove(context, activity.id)
    }

    private fun getTimerIntent(activity: TrackedActivity, flag: Int = PendingIntent.FLAG_UPDATE_CURRENT): PendingIntent?{
        val intent = Intent(context, ActivityTimerCompletedReceiver::class.java ).apply {
            putExtra(ActivityTimerCompletedReceiver.ACTIVITY_ID, activity.id)
        }

        val _flag =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else flag

        return PendingIntent.getBroadcast(context, activity.id.toInt(), intent, _flag)
    }

    private suspend fun cancelTimer(activity: TrackedActivity) {
        val pendingIntent = getTimerIntent(activity, PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
        }

        repository.activityDAO.update(activity.apply { activity.timer = null })
    }




}