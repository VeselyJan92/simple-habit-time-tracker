package com.imfibit.activitytracker.ui.screens.activity

import androidx.lifecycle.*
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.core.activityInvalidationTracker
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.getitdone.database.AppDatabase
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

class TrackedActivityViewModel(val id: Long) : ViewModel() {

    val rep = RepositoryTrackedActivity()

    val recent = MutableLiveData<List<Week>>(listOf())
    val metricToday = MutableLiveData<ComposeString>()
    val metricWeek = MutableLiveData<ComposeString>()
    val metricMonth = MutableLiveData<ComposeString>()
    val metric30Days = MutableLiveData<ComposeString>()

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

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        val activity:TrackedActivity? = rep.activityDAO.getById(id)

        if (activity == null)
            return@launch

        val now = LocalDate.now()
        val today = LocalDate.now()
        val endOfweek = now.with(ChronoField.DAY_OF_WEEK, 7)
        val month = today.withDayOfMonth(1)
        val days30 = today.minusDays(30L)



        this@TrackedActivityViewModel.activity.postValue(activity)

        metricToday.postValue(activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, today, today)
        ))

        val isCheked = activity.type == TrackedActivity.Type.CHECKED

        metricWeek.postValue(activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, endOfweek.minusDays(6), endOfweek),
            fraction = if (isCheked) 7L else null
        ))

        metricMonth.postValue(activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, month, today),
            fraction = if (isCheked) today.lengthOfMonth().toLong() else null
        ))

        metric30Days.postValue(activity.type.getComposeString(
            rep.metricDAO.getMetric(activity.id, days30, today),
            fraction = if (isCheked) 30L else null
        ))

        recent.postValue(rep.getRecentActivity(id, endOfweek, 12))

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
