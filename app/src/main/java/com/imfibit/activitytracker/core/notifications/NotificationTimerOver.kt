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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.receivers.StopActivitySessionReceiver
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

object NotificationTimerOver {
    val ID_BASE = 1000
    val CHANNEL_ID = "NotificationTimerOver"

    fun show(context: Context, activity: TrackedActivity){

        val openAppIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopTimerIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, StopActivitySessionReceiver::class.java).apply { putExtra("activity_id", activity.id) },
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_timer_title)+ ": " + activity.name)
            .setContentText(context.getText(R.string.notification_timer_text))
            .setSmallIcon(R.drawable.ic_baseline_stop_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(openAppIntent, true)
            .addAction(R.drawable.ic_baseline_stop_24, context.getString(R.string.notification_timer_stop), stopTimerIntent)
            .setSound(sound);

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(ID_BASE + activity.id.toInt(), builder.build())
        }
    }


    fun remove(context: Context, activityId: Long){
        val notificationManager =  NotificationManagerCompat.from(context)
        notificationManager.cancel( ID_BASE + activityId.toInt())
    }

    fun createChannel(context: Context){
        val name = context.getString(R.string.notification_timer_channel_name)
        val descriptionText = context.getString(R.string.notification_timer_channel_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)


        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        channel.setSound(soundUri, audioAttributes)

        notificationManager.createNotificationChannel(channel)
    }

}