package com.imfibit.activitytracker.ui.screens.activity


import android.util.Log
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.core.activityTables
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.core.invalidationStateFlow
import com.imfibit.activitytracker.core.registerInvalidationTracker
import com.imfibit.activitytracker.core.services.TrackTimeService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject


@Immutable
data class TrackedActivityState(
    val activity: TrackedActivity,
    val timers: List<PresetTimer>,
    val recent: List<RepositoryTrackedActivity.Month>,
    val months: List<MetricWidgetData>,
    val groups: List<TrackerActivityGroup>,
    val graph: List<RepositoryTrackedActivity.Week>,
    val challengeMetric: Long,
)

@HiltViewModel
class TrackedActivityViewModel @Inject constructor(
    private val timerService: TrackTimeService,
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    var deleted = false

    override fun onCleared() {
        Log.e("TrackedActivityViewModel", "clear")
        super.onCleared()

        if (!activityName.value.isNullOrBlank() && !deleted)
            runBlocking(Dispatchers.IO) {
                updateName(activityName.value!!)
            }
    }

    val id = savedStateHandle.toRoute<Destinations.ScreenActivity>().activityId

    private lateinit var source: MonthsPagingSource

    init {
        registerInvalidationTracker(db, *activityTables){
            source.invalidate()
        }
    }

    val months = Pager(PagingConfig(MonthsPagingSource.PAGE_SIZE) ){
        source = MonthsPagingSource(rep, id)
        source
    }.flow.cachedIn(viewModelScope)

    //For better edittext performance save the name of the activity when user is done with the screen
    val activityName = mutableStateOf<String?>(null)

    val data = invalidationStateFlow(db, null, *activityTables) {
        val activity: TrackedActivity =
            rep.activityDAO.tryGetById(id) ?: return@invalidationStateFlow null

        //Ignore subsequent changes
        viewModelScope.launch(Dispatchers.Main) {
            activityName.value = activity.name
        }


        /*
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
        )*/

        val recent = listOf(
            //rep.getRecentActivityM(id, YearMonth.now().minusMonths(2)),
            rep.getMonthData(id, YearMonth.now().minusMonths(1)),
            rep.getMonthData(id, YearMonth.now()),
        )

        // val graph = rep.getWeeks(id)
        val graph = listOf<RepositoryTrackedActivity.Week>()


        val months = rep.metricDAO.getMetricByMonth(
            activity.id,
            YearMonth.now(), 6
        ).map {
            val color = if (activity.goal.range == TimeRange.MONTHLY)
                if (activity.goal.value <= it.metric) Colors.Completed else Colors.NotCompleted
            else
                Colors.AppAccent

            val metric: ContextString = if (activity.type == TrackedActivity.Type.CHECKED) {
                { "${it.metric} / ${it.from.month.length(it.from.isLeapYear)}" }
            } else {
                activity.type.getLabel(it.metric)
            }

            MetricWidgetData(
                label = { it.from.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) },
                value = metric,
                color = color
            )
        }.reversed()


        val groups = db.groupDAO().getAll()

        val timers = rep.timers.getAll(activity.id).toMutableList()

        val challengeMetric =
            rep.getChallengeMetric(activity.id, activity.challenge.from, activity.challenge.to)

        TrackedActivityState(
            activity, timers, recent, months, groups, graph, challengeMetric
        )
    }


    private suspend fun updateName(name: String) {
        val activity = rep.activityDAO.getById(id)
        rep.activityDAO.update(activity.copy(name = name))
    }

    fun refreshName(name: String) = launchIO {
        activityName.value = name
    }

    fun addTimer(timer: PresetTimer) = launchIO {
        rep.timers.insert(timer)
    }

    fun swapTimer(from: LazyListItemInfo, to: LazyListItemInfo) {
        val timers = (data.value?.timers ?: return)
            .toMutableList()
            .apply { swap(from.index, to.index) }
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
        val activity = rep.activityDAO.getById(id)
        rep.activityDAO.update(activity.copy(goal = goal))
    }


    fun scheduleTimer(timer: PresetTimer) = launchIO {
        timerService.startWithTimer(rep.activityDAO.getById(id), timer)
    }

    fun deleteActivity(activity: TrackedActivity) = launchIO {
        Log.e("XXX", "DELETE")

        db.withTransaction {
            if (activity.type == TrackedActivity.Type.TIME)
                timerService.cancelSession(activity)

            deleted = true
            rep.activityDAO.deleteById(activity.id)
        }

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

    suspend fun getChallengeMetric(from: LocalDate?, to: LocalDate?): Long {
        return rep.getChallengeMetric(id, from, to)
    }

    fun updateActivity(activity: TrackedActivity) = launchIO {
        rep.activityDAO.update(activity)
    }


}


class MonthsPagingSource(
    val rep: RepositoryTrackedActivity,
    val activityId: Long
) : PagingSource<Int, RepositoryTrackedActivity.Month>() {

    companion object {
        const val PAGE_SIZE = 6
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryTrackedActivity.Month> {

        val month = params.key ?: 0

        val months = List(PAGE_SIZE){
            rep.getMonthData(activityId, YearMonth.now().minusMonths((month * PAGE_SIZE +  it).toLong()))
        }

        return LoadResult.Page(
            data = months,
            prevKey =   null,
            nextKey = month + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, RepositoryTrackedActivity.Month>): Int? {
        return 0
    }
}



