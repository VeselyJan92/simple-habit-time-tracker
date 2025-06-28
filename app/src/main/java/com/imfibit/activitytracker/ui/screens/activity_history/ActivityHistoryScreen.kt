package com.imfibit.activitytracker.ui.screens.activity_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
) {
    val vm = hiltViewModel<TrackedActivityHistoryVM>()
    val recordViewModel = hiltViewModel<RecordViewModel>()
    val activity by vm.activity.collectAsState(initial = null)
    val months = vm.months

    ScreenActivityHistory(
        activity = activity,
        months = months,
        onDayClicked = { activity, date -> RecordNavigatorImpl.onDayClicked(nav, activity, date) },
        onDayLongClicked = { activity, date ->
            RecordNavigatorImpl.onDaylongClicked(
                nav = nav,
                recordViewModel = recordViewModel,
                activity = activity,
                date = date
            )
        },
        onNavigateBack = { nav.popBackStack() }
    )
}

@Composable
fun ScreenActivityHistory(
    activity: TrackedActivity?,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            SimpleTopBar(
                title = stringResource(id = R.string.screen_title_record_history),
                onBack = onNavigateBack
            )
        },
        content = { contentPadding ->
            if (activity != null) {
                HistoryList(
                    paddingValues = contentPadding,
                    activity = activity,
                    months = months,
                    onDayClicked = onDayClicked,
                    onDayLongClicked = onDayLongClicked
                )
            }

        },
        containerColor = Colors.AppBackground,
    )
}

@Composable
private fun HistoryList(
    paddingValues: PaddingValues,
    activity: TrackedActivity,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
) {

    val monthsData = months.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        reverseLayout = true
    ) {
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
) {
    Surface(
        shadowElevation = 2.dp,
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
