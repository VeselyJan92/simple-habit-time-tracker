package com.imfibit.activitytracker.ui.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.services.OverviewWidgetService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.widgets.WidgetOverview.Companion.METRIC_TODAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConfActivityWidgetOverviewVM @Inject constructor(
    val database: AppDatabase,
    val service: OverviewWidgetService,
) : WidgetPickerVM(database) {

    override fun setupWidget(glanceId: GlanceId, activityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            service.setupWidget(glanceId, activityId)
        }
    }
}

class ConfActivityWidgetOverview : ActivityChoseTrackedActivity() {
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

        val DELETED = booleanPreferencesKey("DELETED")

        fun keyValue(index: Int) = stringPreferencesKey("METRIC_${index}_VALUE")
        fun keyLabel(index: Int) = stringPreferencesKey("METRIC_${index}_LABEL")
        fun keyColor(index: Int) = stringPreferencesKey("METRIC_${index}_COLOR")
    }

    override val sizeMode = SizeMode.Responsive(
        buildSet {
            repeat(10) {
                add(DpSize(width = 60.dp + (it * 30).dp, height = 50.dp))
            }
        }
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deletedText = context.getString(R.string.widget_error_activity_deleted)

        provideContent {
            val prefs = currentState<Preferences>()

            if (prefs[DELETED] == true) {
                ActivityDeleted(deletedText)
            } else {
                WidgetContent(prefs)
            }
        }
    }
}

@Composable
private fun WidgetContent(prefs: Preferences) {
    val size = LocalSize.current

    Column(
        modifier = GlanceModifier
            .padding(8.dp)
            .fillMaxSize()
            .background(color = Color.White),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                modifier = GlanceModifier.width(if (size.width > 210.dp) size.width - 80.dp else size.width),
                text = prefs[WidgetOverview.Companion.ACTIVITY_NAME] ?: "",
                maxLines = 1,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(Color.Black)
                )
            )

            if (size.width > 210.dp) {
                Spacer(modifier = GlanceModifier.defaultWeight())

                Text(
                    text = "Today:",
                    style = TextStyle(
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(Color.Black)
                    )
                )

                Spacer(GlanceModifier.width(4.dp))

                Text(
                    text = prefs[METRIC_TODAY] ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        color = ColorProvider(Color.Black)
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth()
        ) {

            val items = ((size.width - 20.dp) / 35.dp).toInt()

            repeat(items) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = prefs[WidgetOverview.Companion.keyLabel(it)] ?: "",
                        style = TextStyle(
                            color = ColorProvider(Color.Black),
                            fontWeight = if (it == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )

                    //So far there is no cross compatible way to have round corners and color
                    val resource = when (prefs[WidgetOverview.Companion.keyColor(it)]) {
                        "#FF9800" -> R.drawable.widget_backgoround_orange
                        "#E0E0E0" -> R.drawable.widget_backgoround_gray
                        "#59BF2D" -> R.drawable.widget_backgoround_green
                        else -> R.drawable.widget_backgoround_gray
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
                            text = prefs[WidgetOverview.Companion.keyValue(it)] ?: ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityDeleted(deletedText: String) {
    Box(
        modifier = GlanceModifier
            .padding(5.dp)
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = deletedText,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = ColorProvider(Color.Black)
            )
        )
    }
}