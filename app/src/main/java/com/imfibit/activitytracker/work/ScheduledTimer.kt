package com.imfibit.activitytracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException


@HiltWorker
class ScheduledTimer @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: RepositoryTrackedActivity
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activity = try {
            repository.activityDAO.getById(inputData.getLong("activity_id", 0))
        }catch (e: Exception){
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException("Activity ID cannot be zero"))
            return Result.success()
        }

        NotificationTimerOver.show(appContext, activity)

        return Result.success()
    }
}
