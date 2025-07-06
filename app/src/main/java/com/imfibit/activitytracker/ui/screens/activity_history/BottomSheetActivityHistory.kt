package com.imfibit.activitytracker.ui.screens.activity_history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.TrackedActivityMonth
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState
import com.imfibit.activitytracker.ui.viewmodels.RecordNavigatorImpl
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BottomSheetActivityHistory_Preview() = AppTheme {
    val data = (0..10).map {
        DevSeeder.getMonthData(YearMonth.now().minusMonths(it.toLong()))
    }

    BottomSheetActivityHistory(
        sheetState = rememberTestBottomSheetState(),
        activity = DevSeeder.getTrackedActivityTime(),
        months = flowOf(PagingData.from(data)),
        onDayClicked = { _, _ -> },
        onDayLongClicked = { _, _ -> },
        onDismissRequest = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetActivityHistory(
    onDismissRequest: () -> Unit,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    nav: NavHostController,
    activity: TrackedActivity?,
) {
    val recordViewModel = hiltViewModel<RecordViewModel>()

    val haptic = LocalHapticFeedback.current

    BottomSheetActivityHistory(
        activity = activity,
        months = months,
        onDayClicked = { activity, date -> RecordNavigatorImpl.onDayClicked(nav, activity, date) },
        onDayLongClicked = { activity, date ->
            RecordNavigatorImpl.onDaylongClicked(
                nav = nav,
                recordViewModel = recordViewModel,
                activity = activity,
                date = date,
                haptic = haptic
            )
        },
        onDismissRequest = onDismissRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetActivityHistory(
    sheetState: SheetState = rememberAppBottomSheetState(),
    activity: TrackedActivity?,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) = BaseBottomSheet(
    state = sheetState,
    onDismissRequest = onDismissRequest
) {
    Text(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .align(Alignment.CenterHorizontally),
        text = stringResource(id = R.string.screen_title_record_history),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
    )

    if (activity != null) {
        val monthsData = months.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true
        ) {
            items(
                count = monthsData.itemCount,
                key = monthsData.itemKey { it.month },
            ) { lazyItem ->
                val item = monthsData[lazyItem]

                if (item != null) {
                    TrackedActivityMonth(
                        activity = activity,
                        month = item,
                        onDayClicked = onDayClicked,
                        onDayLongClicked = onDayLongClicked
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
