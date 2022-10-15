package com.imfibit.activitytracker.core

import android.app.Application
import com.imfibit.activitytracker.core.services.GlobalWidgetUpdateService
import com.imfibit.activitytracker.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class App : Application(){

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var globalWidgetUpdateService: GlobalWidgetUpdateService


    override fun onCreate() {
        super.onCreate()

        globalWidgetUpdateService.setAlarmManager()

        database.invalidationTracker.addObserver(globalWidgetUpdateService.tracker)
    }

}