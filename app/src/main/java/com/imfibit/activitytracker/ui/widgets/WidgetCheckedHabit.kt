package com.imfibit.activitytracker.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.imfibit.activitytracker.ui.components.Colors

class WidgetCheckedHabitReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WidgetCheckedHabit()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }
}

class WidgetCheckedHabit : GlanceAppWidget() {

    companion object{
        val NAME = stringPreferencesKey("NAME")
        val ACTIVITY_ID = longPreferencesKey("ACTIVITY_ID")
        val CHECKED = booleanPreferencesKey("CHECKED")
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = if (prefs[CHECKED] == true) Colors.Completed else Colors.NotCompleted),

            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = prefs[NAME] ?: "Error",
                style = TextStyle(fontSize = 16.sp, color = ColorProvider(Color.Black), textAlign = TextAlign.Center),
            )
        }
    }
}