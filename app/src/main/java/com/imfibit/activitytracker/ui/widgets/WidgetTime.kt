package com.imfibit.activitytracker.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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

class WidgetTimeReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WidgetTime()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }
}

class WidgetTime : GlanceAppWidget() {

    companion object{
        val NAME = stringPreferencesKey("NAME")
        val TIME = longPreferencesKey("TIME")
        val ACTIVITY_ID = longPreferencesKey("ACTIVITY_ID")
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.White),

            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = prefs[NAME] ?: "Study",
                style = TextStyle(fontSize = 16.sp, color = ColorProvider(Color.Black), textAlign = TextAlign.Center),
            )

            val time = (prefs[TIME] ?: 0L) / 60

            Text(
                text = "${time / 60}h ${time % 60}m" ,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = ColorProvider(Color.Black)),
            )
        }
    }
}