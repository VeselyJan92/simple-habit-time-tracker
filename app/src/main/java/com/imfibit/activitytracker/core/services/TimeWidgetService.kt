package com.imfibit.activitytracker.core.services

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.widgets.WidgetTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TimeWidgetService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {

    suspend fun setupWidget(activityId: Long){

        //TODO there is no other way yet to get glanceId
        val glanceId = GlanceAppWidgetManager(context).getGlanceIds(WidgetTime::class.java).last()

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { pref ->

            pref.toMutablePreferences().apply {

                this[WidgetTime.ACTIVITY_ID] = activityId
                this[WidgetTime.TIME] = database.metricDAO.getMetricToday(activityId)
                this[WidgetTime.NAME] =  database.activityDAO.getById(activityId).name
            }
        }

        WidgetTime().update(context, glanceId)
    }

    suspend fun updateWidgets(){
        GlanceAppWidgetManager(context).getGlanceIds(WidgetTime::class.java).forEach {
            updateWidget(it)
        }
    }

    suspend fun updateWidget(id: GlanceId){

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { pref ->
            pref.toMutablePreferences().apply {

                val activityId = this[WidgetTime.ACTIVITY_ID]

                if (activityId != null){
                    this[WidgetTime.TIME] = database.metricDAO.getMetricToday(activityId)
                    this[WidgetTime.NAME] =  database.activityDAO.getById(activityId).name
                }

            }
        }

        WidgetTime().update(context, id)

    }

}