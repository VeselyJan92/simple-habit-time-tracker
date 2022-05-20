package com.imfibit.activitytracker.ui.widgets

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.services.TimeWidgetService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class WidgetTimeConfActivityViewModel @Inject constructor(
    private val db: AppDatabase,
    private val widgetService: TimeWidgetService
) : AppViewModel() {


    fun setupWidget(activityId: Long) = viewModelScope.launch(Dispatchers.IO) {
        widgetService.setupWidget(activityId)
        Log.e("xxxx", "setup done")
    }

    val data = MutableStateFlow(listOf<TrackedActivity>())


    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.value = db.activityDAO.getAll().filter { it.type == TrackedActivity.Type.TIME }
        }
    }

}

@AndroidEntryPoint
class WidgetTimeConfActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(Activity.RESULT_CANCELED)

        setContent {
            val vm = hiltViewModel<WidgetTimeConfActivityViewModel>()

            val state by vm.data.collectAsState(initial = listOf())

            LazyColumn(){
                state.forEach {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            elevation = 2.dp,
                            modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {


                                runBlocking {
                                    vm.setupWidget(it.id).join()

                                    delay(500)
                                }

                                setResult(Activity.RESULT_OK)

                                Log.e("xxx", "DONE")
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


