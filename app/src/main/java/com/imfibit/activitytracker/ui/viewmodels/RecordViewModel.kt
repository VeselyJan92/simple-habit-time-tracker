package com.imfibit.activitytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity
): ViewModel() {

    fun deleteRecord(recordId: Long, type: TrackedActivity.Type) = viewModelScope.launch {
        when(type){
            TrackedActivity.Type.TIME -> rep.sessionDAO.deleteById(recordId)
            TrackedActivity.Type.SCORE -> rep.scoreDAO.deleteById(recordId)
            TrackedActivity.Type.CHECKED -> rep.completionDAO.deleteById(recordId)
        }
    }

    fun deleteByActivity(recordId: Long, activityId: Long) = viewModelScope.launch {
        deleteRecord(recordId,  rep.activityDAO.getById(activityId).type)
    }

    fun addScore(activityId: Long, time: LocalDateTime, score: Long) = viewModelScope.launch {
        rep.scoreDAO.insert(TrackedActivityScore(0L, activityId, time, score))
    }

    fun updateScore(recordId: Long, time: LocalDateTime, score: Long)  = viewModelScope.launch {
        val item = rep.scoreDAO.getById(recordId).copy(datetime_completed = time, score = score)
        rep.scoreDAO.update(item)
    }

    fun updateSession(recordId: Long, from: LocalDateTime, to: LocalDateTime) = viewModelScope.launch {
        val item = rep.sessionDAO.getById(recordId).copy(datetime_start = from, datetime_end = to)
        rep.sessionDAO.update(item)
    }

    fun insertSession(activityId: Long, from: LocalDateTime, to: LocalDateTime) = viewModelScope.launch {
        rep.sessionDAO.insert(TrackedActivityTime(0L, activityId, from, to))
    }

    fun toggleHabit(activityId: Long, datetime: LocalDateTime) = viewModelScope.launch {
        rep.completionDAO.toggle(activityId, datetime)
    }


}