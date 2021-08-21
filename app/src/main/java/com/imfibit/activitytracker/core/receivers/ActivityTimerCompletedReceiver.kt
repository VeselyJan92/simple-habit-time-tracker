package com.imfibit.activitytracker.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException
import javax.inject.Inject

@AndroidEntryPoint
class ActivityTimerCompletedReceiver() : BroadcastReceiver(){

    companion object{
        val ACTIVITY_ID = "activity_id"
    }

    @Inject
    lateinit var repository: RepositoryTrackedActivity

    override fun onReceive(context: Context, intent: Intent)  = runBlocking {
        Log.e("FIRE", "FIRE")

        val activity = try {
            repository.activityDAO.getById( intent.extras!!.getLong(ACTIVITY_ID))
        }catch (e: Exception){
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException("Activity ID cannot be zero"))
            return@runBlocking
        }

        NotificationTimerOver.show(context, activity)
    }

}