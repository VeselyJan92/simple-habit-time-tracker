package com.imfibit.activitytracker.core.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.core.receivers.ActivityTimerCompletedReceiver
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject





class TrackTimeService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
    @ApplicationContext private val context: Context,
){

    suspend fun startSession(activity: TrackedActivity, start: LocalDateTime = LocalDateTime.now()){
        val updated = activity.copy(inSessionSince = start)
        repository.activityDAO.update(updated)

        NotificationLiveSession.show(context, updated)
    }

    suspend fun commitSession(activity: TrackedActivity){
        if (activity.inSessionSince != null){
            val session = TrackedActivityTime(0, activity.id, activity.inSessionSince!!, LocalDateTime.now())
            repository.sessionDAO.insert(session)
            repository.activityDAO.update(activity.copy(inSessionSince = null))
        }else
            FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException("Committing already committed session"))


        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)

        cancelTimer(activity)
    }

    suspend fun cancelSession(activity: TrackedActivity){
        repository.activityDAO.update(activity.copy(inSessionSince = null))

        cancelTimer(activity)

        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)
    }

    suspend fun startWithTimer(activity: TrackedActivity, timer: PresetTimer){
        startSession(activity.copy(timer = timer.seconds))

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = System.currentTimeMillis() + timer.seconds * 1000

        cancelTimer(activity)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getTimerIntent(activity))
    }

    suspend fun updateSession(activity: TrackedActivity, start: LocalDateTime){
        repository.activityDAO.update(activity.copy(inSessionSince = start, timer = null))

        cancelTimer(activity)

        NotificationLiveSession.show(context, activity)
        NotificationTimerOver.remove(context, activity.id)
    }

    private fun getTimerIntent(activity: TrackedActivity, flag: Int = PendingIntent.FLAG_UPDATE_CURRENT): PendingIntent? {
        val intent = Intent(context, ActivityTimerCompletedReceiver::class.java ).apply {
            putExtra(ActivityTimerCompletedReceiver.ACTIVITY_ID, activity.id)
        }

        return PendingIntent.getBroadcast(context, activity.id.toInt(), intent, flag)
    }

    private fun cancelTimer(activity: TrackedActivity){
        val pendingIntent = getTimerIntent(activity, PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
        }
    }

}