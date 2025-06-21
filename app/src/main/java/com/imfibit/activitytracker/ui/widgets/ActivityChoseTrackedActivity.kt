package com.imfibit.activitytracker.ui.widgets

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow


abstract class WidgetPickerVM constructor(
    private val database: AppDatabase
): BaseViewModel(){
    val data: MutableStateFlow<List<TrackedActivity>> = MutableStateFlow(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.value = getActivities()
        }
    }

    abstract suspend fun setupWidget(glanceId: GlanceId, activityId: Long)

    open fun getActivities() = database.activityDAO().getAll()
}

@AndroidEntryPoint
abstract class ActivityChoseTrackedActivity : ComponentActivity() {

    @Composable
    abstract fun getViewModel() : WidgetPickerVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)

        setContent {
            val vm = getViewModel()

            val activities by vm.data.collectAsState(initial = listOf())
            val glanceId = GlanceAppWidgetManager(this).getGlanceIdBy(intent)

            LazyColumn(){
                activities.forEach {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    runBlocking(Dispatchers.IO) {
                                        if (glanceId != null) {
                                            vm.setupWidget(glanceId, it.id)
                                            delay(500)
                                        }
                                    }
                                    setResult(Activity.RESULT_OK)

                                    finish()
                                },
                        ) {
                            Text(text = it.name, modifier = Modifier.padding(16.dp), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp))
                        }

                    }
                }
            }
        }
    }
}


