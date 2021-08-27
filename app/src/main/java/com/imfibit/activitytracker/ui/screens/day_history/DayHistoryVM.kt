package com.imfibit.activitytracker.ui.screens.day_history

import androidx.lifecycle.*
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayRecordsVM @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val activityId: Long = savedStateHandle["activity_id"] ?: throw IllegalArgumentException()
    val date: LocalDate = LocalDate.parse(savedStateHandle["date"])?: throw IllegalArgumentException()

    val records = MutableLiveData<List<RecordWithActivity>>()

    val tracker = activityInvalidationTracker {
        refresh()
    }

    init {
        db.invalidationTracker.addObserver(tracker)
        refresh()
    }

    override fun onCleared() {
        db.invalidationTracker.removeObserver(tracker)
    }

    fun refresh() = viewModelScope.launch {

        val activity = rep.activityDAO.flowById(activityId).first()

        val from = date.atStartOfDay()
        val to = from.plusDays(1L)


        val data = rep.getRecords(activity.id, activity.type, from, to).map {
            RecordWithActivity(activity, it)
        }

        records.postValue(data)
    }








}
