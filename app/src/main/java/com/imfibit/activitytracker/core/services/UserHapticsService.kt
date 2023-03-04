package com.imfibit.activitytracker.core.services

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserHapticsService @Inject constructor(
    @ApplicationContext private val context: Context,
){

    fun activityFeedback(){
        Log.e("Vibrate", "Vibrate")
        val vibrator  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        }else{
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        vibrator.vibrate(
            VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }
}