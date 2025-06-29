package com.imfibit.activitytracker.ui.screens.activity_history


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.core.activityTables
import com.imfibit.activitytracker.core.registerInvalidationTracker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject


@HiltViewModel
class TrackedActivityHistoryVM @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val id = savedStateHandle.toRoute<Destinations.ScreenActivityHistory>().activityId

    val activity = rep.db.activityDAO().flowById(id);

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


