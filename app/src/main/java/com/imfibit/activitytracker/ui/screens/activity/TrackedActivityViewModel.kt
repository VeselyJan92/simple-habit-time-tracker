package com.imfibit.activitytracker.ui.screens.activity


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.core.invalidationFlow
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.*
import javax.inject.Inject


@Immutable
data class TrackedActivityState(
        val activity: TrackedActivity,
        val timers: MutableList<PresetTimer>,
        val recent: List<Week>,
        val months: List<MetricWidgetData>,
        val groups: List<TrackerActivityGroup>,
        val metricToday: ComposeString,
        val metricWeek:  ComposeString,
        val metricMonth:  ComposeString,
        val metric30Days:  ComposeString,
)

@HiltViewModel
class TrackedActivityViewModel @Inject constructor(
    private val timerService: TrackTimeService,
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val savedStateHandle: SavedStateHandle
) : AppViewModel() {

    val id: Long = savedStateHandle["activity_id"] ?: throw IllegalArgumentException()

    val data = invalidationFlow(db, viewModelScope){
        val activity: TrackedActivity = rep.activityDAO.flowById(id).firstOrNull() ?: return@invalidationFlow null
        
        val now = LocalDate.now()
        val today = LocalDate.now()
        val endOfWeek = now.with(ChronoField.DAY_OF_WEEK, 7)
        val month = today.withDayOfMonth(1)
        val days30 = today.minusDays(30L)


        val metricToday = activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, today, today)
        )

        val isChecked = activity.type == TrackedActivity.Type.CHECKED

        val metricWeek = activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, endOfWeek.minusDays(6), endOfWeek),
            fraction = if (isChecked) 7L else null
        )

        val metricMonth = activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, month, today),
            fraction = if (isChecked) today.lengthOfMonth().toLong() else null
        )

        val metric30Days = activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, days30, today),
            fraction = if (isChecked) 30L else null
        )

        val recent = rep.getRecentActivity(id, endOfWeek, 12)

        val months = rep.metricDAO.getMetricByMonth(
            activity.id,
            YearMonth.now(),6
        ).map {
            val color = if (activity.goal.range == TimeRange.MONTHLY)
                if (activity.goal.value <= it.metric) Colors.Completed else Colors.NotCompleted
            else
                Colors.AppAccent

            val metric:ComposeString = if (activity.type == TrackedActivity.Type.CHECKED)
            {{ "${it.metric} / ${it.from.month.length(it.from.isLeapYear)}" }}
            else
            { activity.type.getComposeString(it.metric) }

            MetricWidgetData.Labeled(
                label = { it.from.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) },
                metric = metric,
                color = color
            )
        }


        val groups = db.groupDAO.getAll()

        val timers = rep.timers.getAll(activity.id).toMutableList()

        TrackedActivityState(
            activity, timers, recent, months, groups, metricToday, metricWeek, metricMonth, metric30Days
        )
    }


    fun updateName(name: String) = launchIO {
        val activity = rep.activityDAO.getById(id)
        rep.activityDAO.update(activity.copy(name = name))
    }

    fun addTimer(timer: PresetTimer) = launchIO {
        rep.timers.insert(timer)
    }

    fun reorganizeTimers(items: List<PresetTimer>) = launchIO {
        val timers = items.mapIndexed{index, item -> item.copy(position = index) }.toTypedArray()
        rep.timers.updateAll(*timers)
    }


    fun deleteTimer(timer: PresetTimer) = launchIO {
        rep.timers.delete(timer)
    }


    fun updateGoal(goal: TrackedActivityGoal)  = launchIO {
        val activity = rep.activityDAO.getById(id)
        rep.activityDAO.update(activity.copy(goal = goal))
    }


    fun scheduleTimer(timer: PresetTimer) = launchIO {
        timerService.startWithTimer(rep.activityDAO.getById(id), timer)
    }

    fun deleteActivity(activity: TrackedActivity) = launchIO {
        rep.activityDAO.deleteById(activity.id)
    }

    fun setGroup(group: TrackerActivityGroup?) = launchIO {
        rep.activityDAO.update(
            rep.activityDAO.getById(id).copy(groupId = group?.id)
        )
    }

    fun commitSession(activity: TrackedActivity) = launchIO {
        timerService.commitSession(activity)
    }

    fun startSession(activity: TrackedActivity, start: LocalDateTime) = launchIO {
        timerService.startSession(activity, start)
    }

    fun updateSession(activity: TrackedActivity, start: LocalDateTime) = launchIO {
        timerService.updateSession(activity, start)
    }

    fun clearRunning(activity: TrackedActivity) = launchIO {
        timerService.cancelSession(activity)
    }


}


