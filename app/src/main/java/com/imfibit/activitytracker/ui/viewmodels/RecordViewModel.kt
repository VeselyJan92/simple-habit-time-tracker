package com.imfibit.activitytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.services.activity.TimeActivityService
import com.imfibit.activitytracker.core.services.activity.ToggleActivityService
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.core.services.UserHapticsService
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val rep: RepositoryTrackedActivity,
    private val timerService: TrackTimeService,
    private val toggleService: ToggleActivityService,
    private val sessionService: TimeActivityService,
    public val hapticsService: UserHapticsService
): AppViewModel() {

    fun deleteRecord(recordId: Long, type: TrackedActivity.Type) = viewModelScope.launch {
        when(type){
            TrackedActivity.Type.TIME -> rep.sessionDAO.deleteById(recordId)
            TrackedActivity.Type.SCORE -> sessionService.deleteByRecord(recordId)
            TrackedActivity.Type.CHECKED -> rep.completionDAO.deleteById(recordId)
        }
    }

    fun deleteByActivity(recordId: Long, activityId: Long) = viewModelScope.launch(Dispatchers.IO)  {
        deleteRecord(recordId,  rep.activityDAO.flowById(activityId).first().type)
    }

    fun addScore(activityId: Long, time: LocalDateTime, score: Long) = viewModelScope.launch {
        rep.scoreDAO.insert(TrackedActivityScore(0L, activityId, time, score))
    }

    fun updateScore(recordId: Long, time: LocalDateTime, score: Long)  = viewModelScope.launch {
        val item = rep.scoreDAO.getById(recordId).copy(datetime_completed = time, score = score)
        rep.scoreDAO.update(item)
    }

    fun updateSession(recordId: Long, from: LocalDateTime, to: LocalDateTime) = viewModelScope.launch(Dispatchers.IO) {
        val item = rep.sessionDAO.getById(recordId).copy(datetime_start = from, datetime_end = to)

        sessionService.updateSession(item)
    }

    fun insertSession(activityId: Long, from: LocalDateTime, to: LocalDateTime) = viewModelScope.launch(Dispatchers.IO)  {
        sessionService.insertSession(TrackedActivityTime(0L, activityId, from, to))
    }

    fun toggleHabit(activityId: Long, datetime: LocalDateTime) = viewModelScope.launch {
        toggleService.toggleActivity(activityId, datetime)
    }

    fun activityTriggered(activity: TrackedActivity) = launchIO {
        when (activity.type) {
            TrackedActivity.Type.TIME -> {
                if (activity.inSessionSince != null){
                    timerService.commitSession(activity)
                }else{
                    timerService.startSession(activity)
                }
            }
            TrackedActivity.Type.SCORE -> rep.scoreDAO.commitScore(activity.id, LocalDateTime.now(), 1)
            TrackedActivity.Type.CHECKED -> toggleService.toggleActivity(activity.id, LocalDateTime.now())
        }
    }


}