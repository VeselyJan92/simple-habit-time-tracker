package com.janvesely.activitytracker.core

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
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StopTrackedActivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", 0)
        GlobalScope.launch {  AppDatabase.activityRep.commitLiveSession(id) }
        AppNotificationManager.removeSessionNotification(context, id)
    }
}

object AppNotificationManager{

    fun showSessionNotification(context: Context, item: TrackedActivity){
        val notificationManager =  NotificationManagerCompat.from(context)

        val remoteViews = RemoteViews(context.packageName, R.layout.notification)

        remoteViews.setTextViewText(R.id.tracked_task_notification_tv_name, item.name);
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

        val intent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_activities_list)
            .createPendingIntent()

        remoteViews.setOnClickPendingIntent(R.id.tracked_task_notification_tv_name, intent)


        val stopIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, StopTrackedActivityReceiver::class.java).apply { putExtra("id", item.id) },
            PendingIntent.FLAG_ONE_SHOT
        )


        remoteViews.setOnClickPendingIntent(R.id.rv_live_tracked_task_btn_stop, stopIntent)

        val customNotification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle("Tracked activity")
            .setUsesChronometer(true)
            .setContent(remoteViews)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(true);


        notificationManager.notify(item.id.toInt(), customNotification.build())
    }

    fun removeSessionNotification(context: Context, activityId: Long){
        val notificationManager =  NotificationManagerCompat.from(context)
        notificationManager.cancel(activityId.toInt())
    }

}