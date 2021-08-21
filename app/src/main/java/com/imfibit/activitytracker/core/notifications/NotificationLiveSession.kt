package com.imfibit.activitytracker.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.receivers.StopActivitySessionReceiver
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject








object NotificationLiveSession{
    val ID_BASE = 0
    val CHANNEL_ID = "NotificationLiveSession"


    fun show(context: Context, item: TrackedActivity){

        if (item.id <= 0){
            throw IllegalArgumentException("Notification without uninitialized TrackedActivity is not allowed ")
        }

        val remoteViews = RemoteViews(context.packageName, R.layout.notification)

        remoteViews.setTextViewText(R.id.tracked_task_notification_tv_name, item.name)


        val lastSuccess: Long = item.inSessionSince!!.toEpochSecond(OffsetDateTime.now().offset) * 1000
        val elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        remoteViews.setChronometer(
            R.id.rv_live_tracked_task_chronometer,
            lastSuccess - elapsedRealtimeOffset,
            null,
            true
        )

        val stopIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            Intent(context, StopActivitySessionReceiver::class.java).apply { putExtra("id", item.id) },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews.setOnClickPendingIntent(R.id.rv_live_tracked_task_btn_stop, stopIntent)

        val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle(context.getString(R.string.notification_session_channel_title))
            .setUsesChronometer(true)
            .setContent(remoteViews)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(true)


       NotificationManagerCompat.from(context).notify(ID_BASE + item.id.toInt(), customNotification.build())
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