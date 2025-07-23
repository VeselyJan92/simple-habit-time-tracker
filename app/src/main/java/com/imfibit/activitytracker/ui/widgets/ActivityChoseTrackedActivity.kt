package com.imfibit.activitytracker.ui.widgets

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.SimpleTopBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class WidgetPickerVM constructor(
    private val database: AppDatabase,
) : BaseViewModel() {
    val data: MutableStateFlow<List<TrackedActivity>> = MutableStateFlow(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.value = getActivities()
        }
    }

    abstract fun setupWidget(glanceId: GlanceId, activityId: Long)

    open fun getActivities() = database.activityDAO().getAll()
}


@AndroidEntryPoint
abstract class ActivityChoseTrackedActivity : ComponentActivity() {

    @Composable
    abstract fun getViewModel(): WidgetPickerVM

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        setContent {
            val vm = getViewModel()
            val activities by vm.data.collectAsState()
            val context = LocalContext.current
            val glanceId = remember(intent) {
                GlanceAppWidgetManager(context).getGlanceIdBy(intent)
            }

            Content(
                activities = activities,
                onSetResult = {
                    if (glanceId != null) {
                        vm.setupWidget(glanceId, it)
                    }

                    setResult(RESULT_OK)
                    finish()
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    activities: List<TrackedActivity>,
    onSetResult: (Long) -> Unit,
) = AppTheme {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.widget_picker_title)
            )
        },
        content = { paddingValues ->
            if (activities.isEmpty()) {
                NoActivitiesMessage(Modifier.padding(paddingValues))
            } else {
                ActivityList(
                    modifier = Modifier.padding(paddingValues),
                    activities = activities,
                    onActivityClick = { activityId ->
                        onSetResult(activityId)
                    }
                )
            }
        }
    )
}


@Composable
fun ActivityList(
    modifier: Modifier = Modifier,
    activities: List<TrackedActivity>,
    onActivityClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(activities, key = { it.id }) { activity ->
            ActivityItem(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )
        }
    }
}

@Composable
fun ActivityItem(
    activity: TrackedActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = activity.name,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NoActivitiesMessage(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_activities_to_display),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}