package com.janvesely.activitytracker.ui.screens.activity

import androidx.lifecycle.*
import com.janvesely.activitytracker.core.activityInvalidationTracker
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.*
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.components.*
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
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

    val recentRecords = MutableLiveData<List<TrackedActivityData>>()

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

        val now = LocalDateTime.now()
        val today = LocalDate.now().atStartOfDay()
        val week = today.with(ChronoField.DAY_OF_WEEK, 1)
        val month = today.withDayOfMonth(1)
        val days30 = today.minusDays(30L)

        val recentStart  = LocalDate.now()
        val recentEnd  = LocalDate.now().minusWeeks(3*4)

        val recentRecordsFrom  = now.minusDays(2L)

        val activity = rep.activityDAO.getById(id)

        this@TrackedActivityViewModel.activity.postValue(activity)

        recent.postValue(rep.getRecentActivity(id, recentStart, recentEnd))

        activity.type.apply {
            metricToday.postValue(format(rep.getMetric(id, this, today, now)))
            metricWeek.postValue(format(rep.getMetric(id, this, week, now)))
            metricMonth.postValue(format(rep.getMetric(id, this, month, now)))
            metric30Days.postValue(format(rep.getMetric(id, this, days30, now)))

            months.postValue(
                TimeRange.MONTHLY.getMonths(6).map {

                    val metric = rep.getMetric(id, this, it.from, it.to)

                    val color = if(activity.goal.range == TimeRange.MONTHLY)
                        if (activity.goal.value <= metric) Colors.Completed else Colors.NotCompleted
                    else
                        Colors.AppAccent

                    MetricWidgetData.Labeled(
                        label = { it.from.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) },
                        metric = { activity.type.format(metric) },
                        color = color
                    )
                }
            )

            recentRecords.postValue(rep.getRecords(id, this, recentRecordsFrom, now))
        }


    }








}
