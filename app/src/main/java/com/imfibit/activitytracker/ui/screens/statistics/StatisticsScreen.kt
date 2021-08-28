package com.imfibit.activitytracker.ui.screens.statistics

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.sumByLong
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.screens.activity_list.Goal
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
fun ScreenStatistics(navController: NavHostController) {
    Scaffold(
        topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_statistics)) },
        content = { ScreenBody() },
        bottomBar = { AppBottomNavigation(navController) },
        backgroundColor = Colors.AppBackground
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScreenBody() = Column {

    val vm = hiltViewModel<StatisticsViewModel>()

    val origin = remember { mutableStateOf(LocalDate.now())}
    val range = remember { mutableStateOf(TimeRange.DAILY)}
    val date = remember { mutableStateOf(LocalDate.now())}

    val state = rememberPagerState(
        pageCount = 100,
        initialPage = 51
    )

    val scope = rememberCoroutineScope()

    Navigation(
        range = range.value,
        date = date.value,
        goTo = {
            scope.launch {
            origin.value = it
            date.value = it
            state.scrollToPage(51)
        } },
        setRange = {
            scope.launch {
                origin.value = LocalDate.now()
                date.value = LocalDate.now()
                range.value = it
                state.scrollToPage(51)
            }

        }
    )

    HorizontalPager(
        state = state,
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.Top
    ) { page ->

        val relativePage = (51 - page).toLong()

        val interval = range.value.getBoundaries(
            when (range.value){
                TimeRange.DAILY -> origin.value.minusDays(relativePage)
                TimeRange.WEEKLY -> origin.value.minusWeeks(relativePage)
                TimeRange.MONTHLY -> origin.value.minusMonths(relativePage)
            }
        )

        Column {

            NavigationTitle(
                range = range.value,
                rangeDate = interval.first,
            )

            val data = remember(range, origin) {
                mutableStateOf(mapOf<TrackedActivity.Type, List<ActivityWithMetric>>())
            }

            LaunchedEffect(range, origin, date, page) {
                data.value = vm.getPageData(interval.first, interval.second)
            }

            if (data.value.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.White), elevation = 2.dp
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_records), style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    BlockTimeTracked(data.value[TrackedActivity.Type.TIME], range.value)
                    BlockScores(data.value[TrackedActivity.Type.SCORE], range.value)
                    BlockCompleted(data.value[TrackedActivity.Type.CHECKED], range.value, date.value)
                }
            }
        }

    }
}

@Composable
private fun Navigation(
    range: TimeRange,
    date: LocalDate,
    goTo: (LocalDate) -> Unit,
    setRange: (TimeRange) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            Modifier.padding(8.dp)
        ) {

            TimeRange.values().forEach { timeRange ->
                val color = if (range == timeRange)
                    Colors.ChipGraySelected
                else
                    Colors.ChipGray

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(30.dp)
                        .background(color, RoundedCornerShape(50))
                        .clickable(onClick = {
                            setRange(timeRange)
                        }),

                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = timeRange.label))
                }
            }

            Spacer(modifier = Modifier.width(50.dp))

            val context = LocalContext.current

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))
                    .clickable(
                        onClick = {
                            DatePickerDialog(
                                context,
                                0,
                                { _, i, i2, i3 -> goTo(LocalDate.of(i, i2 + 1, i3)) },
                                date.year,
                                date.month.value - 1,
                                date.dayOfMonth
                            ).show()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun NavigationTitle(
    range: TimeRange,
    rangeDate: LocalDate
) {
    Surface(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .height(40.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Icon(Icons.Default.ArrowLeft, contentDescription = null)

            Spacer(Modifier.weight(1f))

            Text(
                text = range.getDateLabel(rangeDate),
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.weight(1f))

            Icon(Icons.Default.ArrowRight, contentDescription = null)
        }

    }

}

@Composable
private fun Header(title: String, icon: ImageVector, last: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Modifier.padding(end = 16.dp)
        Icon(icon, modifier = Modifier.padding(end = 8.dp), contentDescription = null)

        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.W600,
                fontSize = 20.sp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        last?.invoke()
    }

}

@Composable
private fun BlockTimeTracked(
    data: List<ActivityWithMetric>?,
    range: TimeRange
) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(8.dp)) {
            Header(title = stringResource(id = R.string.time), icon = Icons.Default.Timer) {
                Box(
                    modifier = Modifier
                        .size(60.dp, 25.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    val sum =
                        TrackedActivity.Type.TIME.getComposeString(data.sumByLong { it.metric })
                            .invoke()

                    Text(
                        sum, style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            data.forEach {
                Row(Modifier.padding(start = 8.dp, end = 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range)
                        Goal(label = it.activity.formatGoal())


                    BaseMetricBlock(
                        metric = it.activity.type.getComposeString(it.metric).invoke(),
                        color = Colors.AppAccent,
                        metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )

                }

                Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }
        }
    }

}


@Composable
private fun BlockScores(
    data: List<ActivityWithMetric>?,
    range: TimeRange
) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = stringResource(id = R.string.score), Icons.Default.Score)

            data.forEach {
                Row(Modifier.padding(start = 8.dp, end = 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range)
                        Goal(label = it.activity.formatGoal())

                    BaseMetricBlock(
                        metric = it.metric.toString(),
                        color = Colors.AppAccent,
                        metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }

        }
    }
}


@Composable
private fun BlockCompleted(
    data: List<ActivityWithMetric>?,
    range: TimeRange,
    date: LocalDate
) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = stringResource(id = R.string.habbits), Icons.Default.AssignmentTurnedIn)

            data.forEach {
                Row(Modifier.padding(start = 8.dp, end = 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range && it.activity.goal.range != TimeRange.DAILY)
                        Goal(label = it.activity.formatGoal())

                    val label =
                        if (it.activity.type == TrackedActivity.Type.CHECKED && range == TimeRange.DAILY)
                            stringResource(id = R.string.yes).toUpperCase()
                        else
                            "${it.metric} / ${range.getNumberOfDays(date)}"

                    BaseMetricBlock(
                        metric = label,
                        color = Colors.AppAccent, metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }
        }
    }

}

