package com.imfibit.activitytracker.ui.screens.statistics

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.screens.activity_list.Goal
import com.imfibit.activitytracker.database.AppDatabase
import java.time.LocalDate


@Composable
fun ScreenStatistics(navController: NavController){
    Scaffold(
        topBar = { TrackerTopAppBar("Statistika") },
        bodyContent = { ScreenBody() },
        backgroundColor = Colors.AppBackground,
    )
}


@Composable
private fun ScreenBody() = Column {
    val clock = AmbientAnimationClock.current

    val state = remember { StatisticsState(LocalDate.now(), TimeRange.DAILY, clock) }

    Log.e("PAGE", "COMPOSE")

    Navigation(state)

    Pager(state = state.pager, modifier = Modifier.padding(bottom = 8.dp)) {

        val interval = state.getRange(this.page)

        val data = remember(state.range, state.origin) {
            mutableStateOf(mapOf<TrackedActivity.Type, List<ActivityWithMetric>>())
        }

        LaunchedEffect(state.range, state.origin) {
            data.value = AppDatabase.activityRep.metricDAO.getActivitiesWithMetric(
                interval.first,
                interval.second
            ).groupBy {
                it.activity.type
            }
        }

        if (data.value.isEmpty()){
            Surface(modifier = Modifier.padding(8.dp).background(Color.White), elevation = 2.dp) {
                Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
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
            ScrollableColumn(Modifier) {
                BlockTimeTracked(data.value[TrackedActivity.Type.SESSION], state)
                BlockScores(data.value[TrackedActivity.Type.SCORE], state)
                BlockCompleted(data.value[TrackedActivity.Type.CHECKED], state)
            }
        }

    }
}

@Composable
private fun Navigation(state: StatisticsState) {

    Surface(modifier = Modifier.padding(8.dp).background(Color.White), elevation = 2.dp) {
        Column{

            Row(Modifier.padding(horizontal = 8.dp).padding(top = 8.dp)) {

                TimeRange.values().forEach {timeRange ->
                    val color = if (state.range == timeRange)
                        Colors.ChipGraySelected
                    else
                        Colors.ChipGray

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end =  8.dp)
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

                val context = AmbientContext.current

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(30.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50))
                        .clickable(
                            onClick = {
                                DatePickerDialog(context, 0,
                                    { _, i, i2, i3 ->
                                       state.setCustomDate(LocalDate.of(i, i2+1, i3))
                                    },
                                    state.date.year,  state.date.month.value-1,  state.date.dayOfMonth
                                ).show()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarToday)
                }
            }

            Divider(Modifier.padding(top = 8.dp))

            NavigationTitle(state)
        }
    }
}

@Composable
private fun NavigationTitle(state: StatisticsState){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ArrowLeft.copy(
            defaultHeight = 30.dp,
            defaultWidth = 30.dp
        ))

        Spacer(Modifier.weight(1f))

        Text(
            text = state.range.getDateLabel(state.date),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.weight(1f))

        Icon(Icons.Default.ArrowRight.copy(defaultHeight = 30.dp, defaultWidth = 30.dp))
    }
}

@Composable
private fun Header(title: String, icon: ImageVector, last: @Composable (() -> Unit)? = null){
    Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {

        Modifier.padding(end = 16.dp)
        Icon(icon, Modifier.padding(end = 8.dp))

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
               /* Box(
                    modifier = Modifier.size(60.dp, 25.dp).background(Colors.ChipGray, RoundedCornerShape(50)),
                    alignment = Alignment.Center
                ){
                    Text("54:23",  style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ))
                }*/
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


/*
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.today),
                    color = Colors.AppAccent,
                    width = 40.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold)
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.week),
                    color = Colors.AppAccent,
                    width = 40.dp
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.month),
                    color = Colors.ChipGray,
                    width = 40.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                )
                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 40.dp
                )

                LabeledMetricBlock(
                    metric = "20:00",
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 40.dp
                )
            }
*/
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

