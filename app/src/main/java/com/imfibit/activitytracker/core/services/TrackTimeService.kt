package com.imfibit.activitytracker.core.services

import android.content.Context
import androidx.work.Data
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
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject





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

        val tag = timerWorkId(activity)

        val manager = WorkManager.getInstance(context)
        manager.cancelAllWorkByTag(tag)

        NotificationLiveSession.remove(context, activity.id)
        NotificationTimerOver.remove(context, activity.id)
    }

    suspend fun startWithTimer(activity: TrackedActivity, timer: PresetTimer){
        startSession(activity.copy(timer = timer.seconds))

        val tag = timerWorkId(activity)

        val notifier = OneTimeWorkRequestBuilder<ScheduledTimer>()
            .setInputData(Data.Builder().putLong("activity_id", activity.id).build())
            .setInitialDelay(timer.seconds.toLong(), TimeUnit.SECONDS)
            .addTag(tag)
            .build()

        val manager = WorkManager.getInstance(context)
        manager.cancelAllWorkByTag(tag)
        manager.enqueue(notifier)
    }

    suspend fun updateSession(activity: TrackedActivity, start: LocalDateTime){
        repository.activityDAO.update(activity.copy(inSessionSince = start))

        NotificationLiveSession.show(context, activity)
        NotificationTimerOver.remove(context, activity.id)
    }

}