package com.imfibit.activitytracker.core

import android.app.Application
import com.imfibit.activitytracker.core.services.WidgetUpdateService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class App : Application(){

    @Inject
    lateinit var widgetUpdateService: WidgetUpdateService

    override fun onCreate() {
        super.onCreate()

        widgetUpdateService.setLiveUpdates()
        widgetUpdateService.setMidnightUpdate()
    }

}