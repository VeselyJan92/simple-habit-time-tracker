package com.imfibit.activitytracker.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.core.services.OverviewWidgetService
import com.imfibit.activitytracker.database.AppDatabase
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
        fun keyColor(index: Int) = stringPreferencesKey("METRIC_${index}_COLOR")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
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
                                    fontWeight = if (it == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )

                            //So far there is no cross compatible way to have round corners and color
                            val resource = when (prefs[keyColor(it)]){
                                "#FF9800" -> com.imfibit.activitytracker.R.drawable.widget_backgoround_orange
                                "#E0E0E0" -> com.imfibit.activitytracker.R.drawable.widget_backgoround_gray
                                "#59BF2D" -> com.imfibit.activitytracker.R.drawable.widget_backgoround_green
                                else -> throw IllegalArgumentException()
                            }

                            Box(
                                modifier = GlanceModifier
                                    .width(35.dp)
                                    .height(20.dp)
                                    .background(ImageProvider(resource)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = GlanceModifier,
                                    style = TextStyle(
                                        color = ColorProvider(Color.Black),
                                        fontWeight = if (it == 0) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    ),
                                    text = prefs[keyValue(it)] ?: "y"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.height(5.dp))


                /*Row {
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
                }*/
            }
        }
    }
}