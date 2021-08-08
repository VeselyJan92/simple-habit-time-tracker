package com.imfibit.activitytracker.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase


class ScheduledTimer(val appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activity = try {
             AppDatabase.activityRep.activityDAO.getById(inputData.getLong("id", 0))
        }catch (e: Exception){
            return Result.success()
        }

        NotificationTimerOver.show(appContext, activity)

        return Result.success()
    }
}
