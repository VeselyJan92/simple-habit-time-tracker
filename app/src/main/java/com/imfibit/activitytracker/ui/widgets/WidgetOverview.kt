package com.imfibit.activitytracker.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.core.services.OverviewWidgetService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ConfActivityWidgetOverviewVM @Inject constructor(
    val database: AppDatabase,
    val service: OverviewWidgetService
): WidgetPickerVM(database){
    override suspend fun setupWidget(glanceId: GlanceId, activityId: Long) = service.setupWidget(glanceId, activityId)
}

class ConfActivityWidgetOverview: ActivityChoseTrackedActivity(){
    @Composable
    override fun getViewModel() = hiltViewModel<ConfActivityWidgetOverviewVM>()
}

class WidgetOverviewReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetOverview()
}

class WidgetOverview : GlanceAppWidget() {

    companion object {
        val ACTIVITY_NAME = stringPreferencesKey("ACTIVITY_NAME")
        val ACTIVITY_ID = longPreferencesKey("ACTIVITY_ID")
        val METRIC_TODAY = stringPreferencesKey("METRIC_TODAY")

        fun keyValue(index: Int) = stringPreferencesKey("METRIC_${index}_VALUE")
        fun keyLabel(index: Int) = stringPreferencesKey("METRIC_${index}_LABEL")
        fun keyColor(index: Int) = intPreferencesKey("METRIC_${index}_COLOR")

    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {

        val prefs = currentState<Preferences>()

        Column(
            modifier = GlanceModifier
                .padding(5.dp)
                .fillMaxSize()
                .background(color = Color.White),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = prefs[ACTIVITY_NAME] ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = androidx.glance.text.TextAlign.Start,
                    color = ColorProvider(Color.Black)
                )
            )

            Spacer(modifier = GlanceModifier.height(5.dp))

            Row(modifier = GlanceModifier.fillMaxWidth()) {

                repeat(5) {

                    if (it != 0)
                        Spacer(modifier = GlanceModifier.defaultWeight())

                    Column(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {

                        Text(
                            text = prefs[keyLabel(it)] ?: "",
                            style = TextStyle(
                                color = ColorProvider(Color.Black),
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )

                        Box(
                            modifier = GlanceModifier
                                .width(35.dp)
                                .height(20.dp)
                                .background(Color(prefs[keyColor(it)] ?: 0))
                                .cornerRadius(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = GlanceModifier,
                                style = TextStyle(
                                    color = ColorProvider(Color.Black),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                text = prefs[keyValue(it)] ?: "y"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(5.dp))


            Row {
                Text(
                    text = "Today:",
                    style = TextStyle(
                        fontSize = 15.sp,
                        textAlign = androidx.glance.text.TextAlign.Start,
                        color = ColorProvider(Color.Black)
                    )
                )

                Spacer(GlanceModifier.width(5.dp))

                Text(
                    text = prefs[METRIC_TODAY] ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = androidx.glance.text.TextAlign.Start,
                        color = ColorProvider(Color.Black)
                    )
                )
            }
        }
    }
}