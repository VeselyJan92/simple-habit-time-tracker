package com.imfibit.activitytracker.ui.screens.activity


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.*

class TrackedActivityVMFactory(private val id: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrackedActivityViewModel(id) as T
    }
}

@Immutable
data class TrackedActivityState(
        val activity: TrackedActivity,
        val recent: List<Week>,
        val months: List<MetricWidgetData>,
        val metricToday: ComposeString,
        val metricWeek:  ComposeString,
        val metricMonth:  ComposeString,
        val metric30Days:  ComposeString,
)

class TrackedActivityViewModel(val id: Long) : ViewModel() {
    val rep = RepositoryTrackedActivity()

    var screenState = MutableLiveData<TrackedActivityState>()

    private val tracker = activityInvalidationTracker {
        refresh()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)

        refresh()
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    private fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        val activity:TrackedActivity = rep.activityDAO.getById(id)

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

        val state = TrackedActivityState(
                activity, recent, months, metricToday, metricWeek, metricMonth, metric30Days
        )

        screenState.postValue(state)
    }
}


