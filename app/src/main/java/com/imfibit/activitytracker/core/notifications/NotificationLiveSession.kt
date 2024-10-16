package com.imfibit.activitytracker.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.MainActivity
import java.time.OffsetDateTime


object NotificationLiveSession{
    val ID_BASE = 0
    val CHANNEL_ID = "NotificationLiveSession"


    fun show(context: Context, trackedActivity: TrackedActivity){

        if (trackedActivity.id <= 0){
            throw IllegalArgumentException("Notification without uninitialized TrackedActivity is not allowed ")
        }

        val remoteViews = RemoteViews(context.packageName, R.layout.notification)

        remoteViews.setTextViewText(R.id.tracked_task_notification_tv_name, trackedActivity.name)


        val lastSuccess: Long = trackedActivity.inSessionSince!!.toEpochSecond(OffsetDateTime.now().offset) * 1000
        val elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        remoteViews.setChronometer(
            R.id.rv_live_tracked_task_chronometer,
            lastSuccess - elapsedRealtimeOffset,
            null,
            true
        )

        /*

        val flag =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val stopIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            Intent(context, StopActivitySessionReceiver::class.java).apply { putExtra("activity_id", item.id) },
            flag
        )

        */

        val contentIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.NOTIFICATION_NAVIGATE_TO_ACTIVITY, trackedActivity.id)
            },
            PendingIntent.FLAG_IMMUTABLE
        )


        val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle(context.getString(R.string.notification_session_channel_title))
            .setUsesChronometer(true)
            .setContent(remoteViews)
            .setContentIntent(contentIntent)
            .setCustomBigContentView(remoteViews)
            .setSilent(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(false)


       NotificationManagerCompat.from(context).notify(ID_BASE + trackedActivity.id.toInt(), customNotification.build())
    }


    fun remove(context: Context, activityId: Long){
        val notificationManager =  NotificationManagerCompat.from(context)
        notificationManager.cancel(activityId.toInt())
    }


    fun createChannel(context: Context){
        val name = context.getString(R.string.notification_session_channel_name)
        val descriptionText = context.getString(R.string.notification_session_channel_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.createNotificationChannel(channel)
    }


}