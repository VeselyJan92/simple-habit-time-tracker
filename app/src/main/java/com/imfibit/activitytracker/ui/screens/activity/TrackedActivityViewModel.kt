package com.imfibit.activitytracker.ui.screens.activity


import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.room.InvalidationTracker
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.ASK_FOR_NOTIFICATION
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.core.PreferenceStore
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.activityTables
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Clock


@Immutable
data class TrackedActivityState(
    val activity: TrackedActivity,
    val timers: List<PresetTimer>,
    val recent: List<RepositoryTrackedActivity.Month>,
    val months: List<MetricWidgetData>,
    val groups: List<TrackerActivityGroup>,
    val challengeMetric: Long,
)

@HiltViewModel(assistedFactory = TrackedActivityViewModel.Factory::class)
class TrackedActivityViewModel @dagger.assisted.AssistedInject constructor(
    private val timerService: TrackTimeService,
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    @dagger.assisted.Assisted private val activityId: Long,
    private val appSettings: PreferenceStore,
) : BaseViewModel() {

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(activityId: Long): TrackedActivityViewModel
    }

    val settings = appSettings.dataStore.data

    val months = Pager(PagingConfig(MonthsPagingSource.PAGE_SIZE)) {
        MonthsPagingSource(rep, activityId, db.invalidationTracker)
    }.flow.cachedIn(viewModelScope)


    val data = invalidationStateFlow(db, null, *activityTables) {
        val activity: TrackedActivity =
            rep.activityDAO.tryGetById(activityId) ?: return@invalidationStateFlow null

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val currentMonthStart = LocalDate(today.year, today.monthNumber, 1)
        val prevMonthStart = currentMonthStart.minus(1, DateTimeUnit.MONTH)

        val recent = listOf(
            rep.getMonthData(activityId, prevMonthStart.year, prevMonthStart.monthNumber),
            rep.getMonthData(activityId, currentMonthStart.year, currentMonthStart.monthNumber),
        )


        val months = rep.metricDAO.getMetricByMonth(
            activity.id,
            today.year,
            today.monthNumber,
            6
        ).map {
            val color = if (activity.goal.range == TimeRange.MONTHLY)
                if (activity.goal.value <= it.metric) Colors.Completed else Colors.NotCompleted
            else
                Colors.AppAccent

            val metric: ContextString = if (activity.type == TrackedActivity.Type.CHECKED) {
                {
                    val nextMonth = it.from.plus(1, DateTimeUnit.MONTH)
                    val lengthOfMonth = it.from.daysUntil(nextMonth)
                    "${it.metric} / $lengthOfMonth"
                }
            } else {
                activity.type.getLabel(it.metric)
            }

            MetricWidgetData(
                label = { "asdads" },
                value = metric,
                color = color
            )
        }.reversed()


        val groups = db.groupDAO().getAll()

        val timers = rep.timers.getAll(activity.id).toMutableList()

        val challengeMetric =
            rep.getChallengeMetric(activity.id, activity.challenge.from, activity.challenge.to)

        TrackedActivityState(
            activity, timers, recent, months, groups, challengeMetric
        )
    }


    fun updateName(name: String) = launchIO {
        val activity = rep.activityDAO.getById(activityId)
        rep.activityDAO.update(activity.copy(name = name))
    }

    fun addTimer(timer: PresetTimer) = launchIO {
        rep.timers.insert(timer)
    }

    fun swapTimer(from: LazyListItemInfo, to: LazyListItemInfo) {
        val timers = (data.value?.timers ?: return)
            .toMutableList()
            .apply { swap(from.index - 1, to.index - 1) }
            .mapIndexed { index, item -> item.copy(position = index) }
            .toTypedArray()

        data.value = data.value?.copy(timers = timers.toMutableList())

        launchIO {
            rep.timers.updateAll(*timers)
        }
    }


    fun deleteTimer(timer: PresetTimer) = launchIO {
        rep.timers.delete(timer)
    }


    fun updateGoal(goal: TrackedActivityGoal) = launchIO {
        val activity = rep.activityDAO.getById(activityId)
        rep.activityDAO.update(activity.copy(goal = goal))
    }


    fun scheduleTimer(timer: PresetTimer) = launchIO {
        timerService.startWithTimer(rep.activityDAO.getById(activityId), timer)
    }

    fun deleteActivity(activity: TrackedActivity) = launchIO {
        db.withTransaction {
            if (activity.type == TrackedActivity.Type.TIME)
                timerService.cancelSession(activity)

            rep.activityDAO.deleteById(activity.id)
        }

    }

    fun setGroup(group: TrackerActivityGroup?) = launchIO {
        rep.activityDAO.update(
            rep.activityDAO.getById(activityId).copy(groupId = group?.id)
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

    suspend fun getChallengeMetric(from: LocalDate?, to: LocalDate?): Long {
        return rep.getChallengeMetric(activityId, from, to)
    }

    fun updateActivity(activity: TrackedActivity) = launchIO {
        rep.activityDAO.update(activity)
    }

    fun setAskForNotification() = viewModelScope.launch {
        appSettings.dataStore.edit {
            it[ASK_FOR_NOTIFICATION] = false
        }
    }
}


class MonthsPagingSource(
    val rep: RepositoryTrackedActivity,
    val activityId: Long,
    private val invalidationTracker: InvalidationTracker,
) : PagingSource<Int, RepositoryTrackedActivity.Month>() {

    companion object {
        const val PAGE_SIZE = 12 * 4
    }

    private val observer = object : InvalidationTracker.Observer(activityTables) {
        override fun onInvalidated(tables: Set<String>) {
            invalidate()
        }
    }

    init {
        invalidationTracker.addObserver(observer)
        registerInvalidatedCallback {
            invalidationTracker.removeObserver(observer)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryTrackedActivity.Month> {
        val pageIndex = params.key ?: 0

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentMonthStart = LocalDate(now.year, now.monthNumber, 1)

        val months = List(PAGE_SIZE) {
            val targetMonth =
                currentMonthStart.minus((pageIndex * PAGE_SIZE + it).toLong(), DateTimeUnit.MONTH)
            rep.getMonthData(
                activityId,
                targetMonth.year,
                targetMonth.monthNumber
            )
        }

        return LoadResult.Page(
            data = months,
            prevKey = null,
            nextKey = pageIndex + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, RepositoryTrackedActivity.Month>): Int? {
        return null
    }
}
