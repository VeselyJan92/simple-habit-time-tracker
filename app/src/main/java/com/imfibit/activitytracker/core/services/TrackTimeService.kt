package com.imfibit.activitytracker.core.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.work.ScheduledTimer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class TimerOverBroadcastReceiver() : BroadcastReceiver(){

    companion object{
        val ACTIVITY_ID = "activity_id"
    }

    @Inject lateinit var repository: RepositoryTrackedActivity

    override fun onReceive(context: Context, intent: Intent)  = runBlocking {
        Log.e("FIRE", "FIRE")

        val activity = try {
            repository.activityDAO.getById( intent.extras!!.getLong(ACTIVITY_ID))
        }catch (e: Exception){
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(java.lang.IllegalArgumentException("Activity ID cannot be zero"))
            return@runBlocking
        }

        NotificationTimerOver.show(context, activity)
    }


}





class TrackTimeService @Inject constructor(
    private val repository: RepositoryTrackedActivity,
    @ApplicationContext private val context: Context,
){

    private fun timerWorkId(activity: TrackedActivity) = "activity_timer_${activity.id}"


    suspend fun startSession(activity: TrackedActivity){
        val updated = activity.copy(inSessionSince = LocalDateTime.now())
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

        val tag = timerWorkId(activity)

        val manager = WorkManager.getInstance(context)
        manager.cancelAllWorkByTag(tag)
    }

    suspend fun cancelSession(activity: TrackedActivity){
        repository.activityDAO.update(activity.copy(inSessionSince = null))

        val pendingIntent = PendingIntent.getService(context, activity.id.toInt(), getTimerIntent(activity), PendingIntent.FLAG_NO_CREATE)


        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)

        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)
    }

    suspend fun startWithTimer(activity: TrackedActivity, timer: PresetTimer){
        startSession(activity.copy(timer = timer.seconds))

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = System.currentTimeMillis() + timer.seconds * 1000

        //alarmManager.cancel(pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, PendingIntent.getService(context, activity.id.toInt(), getTimerIntent(activity), 0))

    }

    suspend fun updateSession(activity: TrackedActivity, start: LocalDateTime){
        repository.activityDAO.update(activity.copy(inSessionSince = start))

        NotificationLiveSession.show(context, activity)
        NotificationTimerOver.remove(context, activity.id)
    }

    private fun getTimerIntent(activity: TrackedActivity): Intent {
        return Intent(context, TimerOverBroadcastReceiver::class.java ).apply {
            putExtra(TimerOverBroadcastReceiver.ACTIVITY_ID, activity.id)
        }
    }

}