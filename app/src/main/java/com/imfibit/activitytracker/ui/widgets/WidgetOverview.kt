package com.imfibit.activitytracker.ui.widgets

import android.annotation.SuppressLint
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
import androidx.glance.LocalContext
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
import androidx.glance.preview.ExperimentalGlancePreviewApi
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
import com.imfibit.activitytracker.ui.widgets.WidgetOverview.Companion.MAX_HISTORY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalGlancePreviewApi::class)
@androidx.glance.preview.Preview(320, 90)
@Composable
private fun WidgetContentPreview() {
    WidgetPreview()
}

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
        const val MAX_HISTORY = 10

        val ACTIVITY_NAME = stringPreferencesKey("ACTIVITY_NAME")
        val ACTIVITY_ID = longPreferencesKey("ACTIVITY_ID")
        val METRIC_TODAY = stringPreferencesKey("METRIC_TODAY")

        val DELETED = booleanPreferencesKey("DELETED")

        fun keyValue(index: Int) = stringPreferencesKey("METRIC_${index}_VALUE")
        fun keyLabel(index: Int) = stringPreferencesKey("METRIC_${index}_LABEL")
        fun keyColor(index: Int) = stringPreferencesKey("METRIC_${index}_COLOR")
    }

    val sizes = buildSet {
        repeat(MAX_HISTORY) {
            add(DpSize(width = 60.dp + (it * 30).dp, height = 50.dp))
        }
    }

    override val sizeMode = SizeMode.Responsive(sizes)

    override val previewSizeMode = SizeMode.Responsive(sizes)

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            WidgetPreview()
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deletedText = context.getString(R.string.widget_error_activity_deleted)

        provideContent {
            val prefs = currentState<Preferences>()

            if (prefs[DELETED] == true) {
                ActivityDeleted(deletedText)
            } else {
                val history = List(10) { index ->
                    WidgetData(
                        color = prefs[keyColor(index)] ?: "",
                        label = prefs[keyLabel(index)] ?: "",
                        value = prefs[keyValue(index)] ?: ""
                    )
                }

                val today = prefs[METRIC_TODAY] ?: ""
                val name = prefs[ACTIVITY_NAME] ?: ""

                WidgetContent(name, history, today)
            }
        }
    }
}

private data class WidgetData(
    val color: String,
    val label: String,
    val value: String,
)

@Composable
private fun WidgetPreview() {
    val context = LocalContext.current

    val name = context.getString(R.string.widget_example_name)
    val week = context.getString(R.string.week)
    val today = context.getString(R.string.yes)

    val history = List(10) { index ->
        val metric = (0..7).random()

        WidgetData(
            color = if (metric > 2) "#59BF2D" else "#FF9800",
            label = week,
            value = "$metric/7"
        )
    }

    WidgetContent(
        name = name,
        history = history,
        today = today
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun WidgetContent(
    name: String,
    history: List<WidgetData>,
    today: String,
) {
    val size = LocalSize.current

    Column(
        modifier = GlanceModifier
            .padding(8.dp)
            .fillMaxSize()
            .background(color = Color.White.copy(alpha = 0.9f)),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                modifier = GlanceModifier.width(if (size.width > 210.dp) size.width - 90.dp else size.width),
                text = name,
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
                    text = today.uppercase(),
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

            repeat(minOf(items, MAX_HISTORY)) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = history[it].label,
                        style = TextStyle(
                            color = ColorProvider(Color.Black),
                            fontWeight = if (it == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )

                    //So far there is no cross compatible way to have round corners and color
                    val resource = when (history[it].color) {
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
                            text = history[it].value
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
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