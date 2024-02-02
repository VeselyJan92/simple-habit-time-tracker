package com.imfibit.activitytracker.ui.screens.activity_history


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.services.activity.ToggleActivityService
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject


@HiltViewModel
class TrackedActivityHistoryVM @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val toggleActivityService: ToggleActivityService,
    private val savedStateHandle: SavedStateHandle
) : AppViewModel() {

    val id: Long = savedStateHandle["activity_id"] ?: throw IllegalArgumentException()

    val activity = rep.db.activityDAO().flowById(id);

    val months: Flow<PagingData<RepositoryTrackedActivity.Month>> = Pager(PagingConfig(MonthsPagingSource.PAGE_SIZE), ){
        MonthsPagingSource(rep, id)
    }.flow.cachedIn(viewModelScope)

    fun toggleTrackedActivity(activity: TrackedActivity, date: LocalDateTime) = launchIO {
        toggleActivityService.toggleActivity(activity.id, date)
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
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}


