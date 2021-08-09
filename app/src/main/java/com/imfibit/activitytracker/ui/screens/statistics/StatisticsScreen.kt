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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.sumByLong
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.screens.activity_list.Goal
import java.time.LocalDate


@Composable
fun ScreenStatistics(navController: NavHostController){
    Scaffold(
        topBar = { TrackerTopAppBar("Statistika") },
        content = { ScreenBody() },
        bottomBar = { AppBottomNavigation(navController) },
        backgroundColor = Colors.AppBackground
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScreenBody() = Column {


    val vm = viewModel<StatisticsViewModel>()

    val state = remember { StatisticsState(LocalDate.now(), TimeRange.DAILY) }

    Navigation(state)

    HorizontalPager(state = state.pager, modifier = Modifier
        .padding(bottom = 8.dp)
        .fillMaxHeight(), verticalAlignment = Alignment.Top){  page ->


        val interval = state.getRange(page)

        val data = remember(state.range, state.origin) {
            mutableStateOf(mapOf<TrackedActivity.Type, List<ActivityWithMetric>>())
        }

        LaunchedEffect(state.range, state.origin) {
            data.value = vm.getPageData(interval.first, interval.second)
        }

        if (data.value.isEmpty()){
            Surface(modifier = Modifier
                .padding(8.dp)
                .background(Color.White), elevation = 2.dp) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.no_records), style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }else{
            Column(Modifier.verticalScroll(rememberScrollState())) {
                BlockTimeTracked(data.value[TrackedActivity.Type.TIME], state)
                BlockScores(data.value[TrackedActivity.Type.SCORE], state)
                BlockCompleted(data.value[TrackedActivity.Type.CHECKED], state)
            }
        }

    }
}

@Composable
private fun Navigation(state: StatisticsState) {

    Surface(modifier = Modifier
        .padding(8.dp)
        .background(Color.White), elevation = 2.dp) {
        Column{

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)) {

                TimeRange.values().forEach {timeRange ->
                    val color = if (state.range == timeRange)
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
                                state.setTimeRange(timeRange)
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
                                    { _, i, i2, i3 ->
                                        state.setCustomDate(LocalDate.of(i, i2 + 1, i3))
                                    },
                                    state.date.year,
                                    state.date.month.value - 1,
                                    state.date.dayOfMonth
                                ).show()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            }

            Divider(Modifier.padding(top = 8.dp))

            NavigationTitle(state)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun NavigationTitle(state: StatisticsState){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ArrowLeft, contentDescription = null)

        Spacer(Modifier.weight(1f))

        Text(
            text = state.range.getDateLabel(state.getRange(state.pager.currentPage).first),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.weight(1f))

        Icon(Icons.Default.ArrowRight, contentDescription = null)
    }
}

@Composable
private fun Header(title: String, icon: ImageVector, last: @Composable (() -> Unit)? = null){
    Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {

        Modifier.padding(end = 16.dp)
        Icon(icon, modifier = Modifier.padding(end = 8.dp), contentDescription=null)

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
private fun BlockTimeTracked(data: List<ActivityWithMetric>?, state: StatisticsState) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {
            Header(title = stringResource(id = R.string.time), icon = Icons.Default.Timer){
                Box(
                    modifier = Modifier
                        .size(60.dp, 25.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ){
                    val sum = TrackedActivity.Type.TIME.getComposeString(data.sumByLong { it.metric }).invoke()

                    Text(sum,  style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ))
                }
            }

            data.forEach {
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == state.range)
                        Goal(label = it.activity.formatGoal())


                    BaseMetricBlock(metric = it.activity.type.getComposeString(it.metric).invoke(), color = Colors.AppAccent, metricStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ))

                }

                Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }
        }
    }

}



@Composable
private fun BlockScores(data: List<ActivityWithMetric>?, state: StatisticsState) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = stringResource(id = R.string.score), Icons.Default.Score)

            data.forEach{
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == state.range)
                        Goal(label = it.activity.formatGoal())

                    BaseMetricBlock(metric = it.metric.toString(), color = Colors.AppAccent, metricStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ))
                }

                Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }

        }
    }
}


@Composable
private fun BlockCompleted(data: List<ActivityWithMetric>?, state: StatisticsState) {
    if (data == null) return

    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(8.dp)) {

            Header(title = stringResource(id = R.string.habbits), Icons.Default.AssignmentTurnedIn)

            data.forEach{
                Row(Modifier.padding( start = 8.dp, end= 8.dp)) {
                    Text(text = it.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == state.range && it.activity.goal.range != TimeRange.DAILY)
                        Goal(label = it.activity.formatGoal())

                    val label = if(it.activity.type == TrackedActivity.Type.CHECKED && state.range == TimeRange.DAILY)
                        stringResource(id = R.string.yes).toUpperCase()
                    else
                        "${it.metric} / ${state.range.getNumberOfDays(state.date)}"

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

