package com.imfibit.activitytracker.ui.screens.activity_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackedActivityMonth
import com.imfibit.activitytracker.ui.components.SimpleTopBar
import com.imfibit.activitytracker.ui.viewmodels.RecordNavigatorImpl
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


@Composable
fun ScreenActivityHistory(
    nav: NavHostController,
    scaffoldState: ScaffoldState,
) {
    val vm = hiltViewModel<TrackedActivityHistoryVM>()
    val recordViewModel = hiltViewModel<RecordViewModel>()
    val activity by vm.activity.collectAsState(initial = null)
    val months = vm.months

    ScreenActivityHistory(
        nav = nav,
        scaffoldState = scaffoldState,
        activity = activity,
        months = months,
        onDayClicked = { activity, date -> RecordNavigatorImpl.onDayClicked(nav, activity, date) },
        onDayLongClicked = { activity, date -> RecordNavigatorImpl.onDaylongClicked(nav, recordViewModel ,activity, date) }
    )
}


@Composable
fun ScreenActivityHistory(
    scaffoldState: ScaffoldState,
    nav: NavHostController,
    activity: TrackedActivity?,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit
) {
    Scaffold(
        modifier =  Modifier.safeDrawingPadding(),
        topBar = {
            SimpleTopBar(nav, stringResource(id = R.string.screen_title_record_history))
        },
        content = {
          if (activity!= null){
              HistoryList(
                  activity = activity,
                  months = months,
                  onDayClicked = onDayClicked,
                  onDayLongClicked = onDayLongClicked
              )
          }

        },
        backgroundColor = Colors.AppBackground,
        scaffoldState = scaffoldState
    )
}

@Composable
private fun HistoryList(
    activity: TrackedActivity,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit
) {

    val monthsData = months.collectAsLazyPagingItems()

    LazyColumn(reverseLayout = true) {
        items(
            count = monthsData.itemCount,
            key = monthsData.itemKey { it.month },
        ) { lazyItem ->
            val item = monthsData[lazyItem]

            if (item != null) {
                MonthImpl(activity, item, onDayClicked, onDayLongClicked)
            }
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun MonthImpl(
    activity: TrackedActivity,
    month: RepositoryTrackedActivity.Month,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit
){
    Surface(
        elevation = 2.dp,
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            TrackedActivityMonth(
                activity = activity,
                month = month,
                onDayClicked = onDayClicked,
                onDayLongClicked = onDayLongClicked
            )
        }
    }
}
