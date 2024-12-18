package com.imfibit.activitytracker.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class StopActivitySessionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var service: TrackTimeService

    @Inject
    lateinit var db: AppDatabase

    override fun onReceive(context: Context, intent: Intent) = runBlocking(Dispatchers.IO) {
        val id = intent.getLongExtra("activity_id", 0)

        val activity = db.activityDAO().flowById(id).firstOrNull() ?: throw Exception("Unknown activity")

        service.commitSession(activity)
    }

}