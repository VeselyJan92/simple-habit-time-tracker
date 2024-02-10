package com.imfibit.activitytracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.services.activity.ToggleActivityService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@Composable
fun EditRecord(navControl: NavHostController) {
    val vm = hiltViewModel<DialogEditRecordVM>()
    val data = vm.data.collectAsState(initial = null)

    val record = data.value

    if (record != null){
        when(record){
            is TrackedActivityCompletion -> { }
            is TrackedActivityScore -> DialogScore(
                record = record,
                onUpdate = {time, score -> vm.onUpdate(record.copy(datetime_completed = time, score = score)) ; navControl.navigateUp()  },
                onDelete = {vm.onDelete(record) ; navControl.navigateUp() },
                onDismissRequest = { navControl.navigateUp() }
            )
            is TrackedActivityTime  -> DialogSession(
                record = record,
                onUpdate = { from, to -> vm.onUpdate(record.copy(datetime_start =  from, datetime_end = to)) ; navControl.navigateUp() },
                onDelete = {vm.onDelete(record) ; navControl.navigateUp() },
                onDismissRequest = { navControl.navigateUp() }
            )
        }
    }
}

@HiltViewModel
public class DialogEditRecordVM @Inject constructor(
    private val db: AppDatabase,
    private val toggleActivityService: ToggleActivityService,
    private val savedStateHandle: SavedStateHandle
): AppViewModel(){

    val record: TrackedActivityRecord =  savedStateHandle["record"] ?: throw IllegalArgumentException()

    init {
        if (record is TrackedActivityCompletion){
            throw Exception("Invalid argument")
        }
    }

    val data = MutableStateFlow(record)

    public fun onUpdate(record: TrackedActivityRecord) = launchIO {
        when(record){
            is TrackedActivityCompletion -> {}
            is TrackedActivityScore -> db.scoreDAO().upsert(record)
            is TrackedActivityTime -> db.sessionDAO().upsert(record)
        }
    }

    public fun onDelete(record: TrackedActivityRecord) = launchIO {
        when(record){
            is TrackedActivityCompletion -> {}
            is TrackedActivityScore -> db.scoreDAO().delete(record)
            is TrackedActivityTime -> db.sessionDAO().delete(record)
        }
    }
}