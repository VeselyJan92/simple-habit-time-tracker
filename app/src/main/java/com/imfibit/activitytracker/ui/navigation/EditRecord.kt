package com.imfibit.activitytracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.AppDestination
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EditRecord(
    record: TrackedActivityRecord
) {
    val navigation = hiltViewModel<BackstackViewModel>()

    val vm = hiltViewModel<DialogEditRecordVM, DialogEditRecordVM.Factory> { factory ->
        factory.create(record)
    }

    val data = vm.data.collectAsState(initial = null)
    val currentRecord = data.value

    if (currentRecord != null) {
        when (currentRecord) {
            is TrackedActivityCompletion -> {}
            is TrackedActivityScore -> DialogScore(
                record = currentRecord,
                onUpdate = { time, score ->
                    vm.onUpdate(
                        currentRecord.copy(
                            datetime_completed = time,
                            score = score
                        )
                    );
                },
                onDelete = { vm.onDelete(currentRecord) },
                onDismissRequest = { navigation.popBackStack() }
            )

            is TrackedActivityTime -> DialogSession(
                record = currentRecord,
                onUpdate = { from, to ->
                    vm.onUpdate(
                        currentRecord.copy(
                            datetime_start = from,
                            datetime_end = to
                        )
                    )
                },
                onDelete = { vm.onDelete(currentRecord) },
                onDismissRequest = { navigation.popBackStack() }
            )
        }
    }
}

@HiltViewModel(assistedFactory = DialogEditRecordVM.Factory::class)
class DialogEditRecordVM @AssistedInject constructor(
    private val db: AppDatabase,
    @Assisted private val record: TrackedActivityRecord
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(record: TrackedActivityRecord): DialogEditRecordVM
    }

    init {
        if (record is TrackedActivityCompletion) {
            throw IllegalArgumentException("Invalid argument: TrackedActivityCompletion is not supported")
        }
    }

    val data = MutableStateFlow(record)

    fun onUpdate(record: TrackedActivityRecord) = launchIO {
        when (record) {
            is TrackedActivityCompletion -> {}
            is TrackedActivityScore -> db.scoreDAO().upsert(record)
            is TrackedActivityTime -> db.sessionDAO().upsert(record)
        }
    }

    fun onDelete(record: TrackedActivityRecord) = launchIO {
        when (record) {
            is TrackedActivityCompletion -> {}
            is TrackedActivityScore -> db.scoreDAO().delete(record)
            is TrackedActivityTime -> db.sessionDAO().delete(record)
        }
    }
}
