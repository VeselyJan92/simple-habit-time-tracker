package com.imfibit.activitytracker.core.services

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.widgets.WidgetOverview
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OverviewWidgetService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val rep: RepositoryTrackedActivity,
) {
    suspend fun setupWidget(glanceId: GlanceId, activityId: Long) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { pref ->

            pref.toMutablePreferences().apply {
                this[WidgetOverview.ACTIVITY_ID] = activityId
                updateContent(this, activityId)
            }
        }

        WidgetOverview().update(context, glanceId)
    }

    suspend fun updateWidgets() {
        GlanceAppWidgetManager(context).getGlanceIds(WidgetOverview::class.java).forEach {
            updateWidget(it)
        }
    }

    suspend fun updateWidget(id: GlanceId) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { pref ->
            pref.toMutablePreferences().apply {

                val activityId = this[WidgetOverview.ACTIVITY_ID]

                if (activityId != null)
                    updateContent(this, activityId)
            }
        }

        WidgetOverview().update(context, id)
    }

    private suspend fun updateContent(pref: MutablePreferences, activityId: Long) {
        val activity = database.activityDAO().getByIdOrNull(activityId)

        if (activity != null) {
            val overview = rep.getActivityOverview(activity)

            pref[WidgetOverview.ACTIVITY_NAME] = activity.name
            pref[WidgetOverview.METRIC_TODAY] =
                activity.type.getLabel(overview.today.metric).value(context)

            for ((index, past) in overview.past.reversed().withIndex()) {
                pref[WidgetOverview.keyValue(index)] = past.value.value(context)
                pref[WidgetOverview.keyLabel(index)] = past.label?.value(context) ?: ""
                pref[WidgetOverview.keyColor(index)] =
                    String.format("#%06X", 0xFFFFFF and past.color.toArgb()).uppercase()
            }
        } else {
            pref[WidgetOverview.DELETED] = true
        }
    }
}