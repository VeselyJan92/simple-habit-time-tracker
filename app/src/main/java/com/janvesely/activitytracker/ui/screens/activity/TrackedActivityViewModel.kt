package com.janvesely.activitytracker.ui.screens.activity

import androidx.lifecycle.*
import com.janvesely.activitytracker.core.ComposeString
import com.janvesely.activitytracker.core.activityInvalidationTracker
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.*
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.components.*
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.*


class TrackedActivityVMFactory(private val id: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrackedActivityViewModel(id) as T
    }
}

class TrackedActivityViewModel(val id: Long) : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val recent = MutableLiveData<List<Week>>(listOf())
    val metricToday = MutableLiveData<String>()
    val metricWeek = MutableLiveData<String>()
    val metricMonth = MutableLiveData<String>()
    val metric30Days = MutableLiveData<String>()

    val months = MutableLiveData<List<MetricWidgetData>>()

    val activity = MutableLiveData<TrackedActivity>()

    val tracker = activityInvalidationTracker {
        refresh()
    }

    init {
        AppDatabase.db.invalidationTracker.addObserver(tracker)

        refresh()
    }

    override fun onCleared() {
        AppDatabase.db.invalidationTracker.removeObserver(tracker)
    }

    fun refresh() = viewModelScope.launch {

        val now = LocalDate.now()
        val today = LocalDate.now()
        val week = if(now.dayOfWeek.value == 1) now else now.with(ChronoField.DAY_OF_WEEK, 1).minusWeeks(1L)
        val month = today.withDayOfMonth(1)
        val days30 = today.minusDays(30L)


        val activity = rep.activityDAO.getById(id)
        this@TrackedActivityViewModel.activity.postValue(activity)

        metricToday.postValue(
            activity.type.format(rep.metricDAO.getMetric(activity.id, today, today))
        )

        metricWeek.postValue(
            activity.type.format(rep.metricDAO.getMetric(activity.id, week, today))
        )

        metricMonth.postValue(
            activity.type.format(rep.metricDAO.getMetric(activity.id, month, today))
        )

        metric30Days.postValue(
            activity.type.format(rep.metricDAO.getMetric(activity.id, days30, today))
        )


        val firstDayInWeek  = LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7)

        recent.postValue(rep.getRecentActivity(id, firstDayInWeek, 12))


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
                {{ activity.type.format(it.metric) }}

            MetricWidgetData.Labeled(
                label = { it.from.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) },
                metric = metric,
                color = color
            )
        }

        this@TrackedActivityViewModel.months.postValue(months)

    }


}
