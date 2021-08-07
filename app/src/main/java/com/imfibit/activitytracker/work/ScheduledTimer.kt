package com.imfibit.activitytracker.work

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.imfibit.activitytracker.R
import android.media.RingtoneManager
import android.net.Uri
import com.imfibit.activitytracker.core.AppNotificationManager
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TrackedActivityStopTimer : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("NOTIFIY", "reciever")
        val id = intent.getLongExtra("id", 0)
        if (id <= 0){
            throw IllegalArgumentException("Missing Activity ID")
        }


        GlobalScope.launch {  AppDatabase.activityRep.commitLiveSession(id) }
        AppNotificationManager.removeSessionNotification(context, id)
    }
}



class ScheduledTimer(val appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.e("RUN", "RUN")


        val fullScreenIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        val fullScreenPendingIntent = PendingIntent.getActivity(appContext, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val builder = NotificationCompat.Builder(appContext, "xx")
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setSmallIcon(R.drawable.ic_baseline_stop_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.ic_baseline_stop_24, "continue", fullScreenPendingIntent)
            .setSound(sound);

        with(NotificationManagerCompat.from(appContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(45, builder.build())
        }

        return Result.success()
    }
}
