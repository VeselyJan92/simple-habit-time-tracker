package com.imfibit.activitytracker.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random


class StopTrackedActivityReceiver : BroadcastReceiver() {
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

object AppNotificationManager{

    fun showSessionNotification(context: Context, item: TrackedActivity){

        if (item.id <= 0){
            throw IllegalArgumentException("Notification without uninitialized TrackedActivity is not allowed ")
        }

        val notificationManager =  NotificationManagerCompat.from(context)

        val remoteViews = RemoteViews(context.packageName, R.layout.notification)

        remoteViews.setTextViewText(R.id.tracked_task_notification_tv_name, item.name)
        remoteViews.setChronometer(
            R.id.rv_live_tracked_task_chronometer,
            SystemClock.elapsedRealtime(),
            null,
            true
        )

        val channel = NotificationChannel(
            "tracked_activity",
            "Tracked activity",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)


        val stopIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            Intent(context, StopTrackedActivityReceiver::class.java).apply { putExtra("id", item.id) },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews.setOnClickPendingIntent(R.id.rv_live_tracked_task_btn_stop, stopIntent)

        val customNotification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle("Tracked activity")
            .setUsesChronometer(true)
            .setContent(remoteViews)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(true)


        notificationManager.notify(item.id.toInt(), customNotification.build())
    }

    fun removeSessionNotification(context: Context, activityId: Long){
        val notificationManager =  NotificationManagerCompat.from(context)
        notificationManager.cancel(activityId.toInt())
    }

}