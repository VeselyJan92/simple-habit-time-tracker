package com.janvesely.activitytracker.ui.screens.activity

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.components.*
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.Typography
import com.janvesely.activitytracker.ui.components.dialogs.DialogGoal
import com.janvesely.activitytracker.ui.components.dialogs.DialogInputText
import com.janvesely.activitytracker.ui.components.dialogs.DialogTimeRange
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalFocus
@ExperimentalFoundationApi
@ExperimentalLayout
@Composable
fun TrackedActivityScreen(nav: NavController, vm: TrackedActivityViewModel) {
    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    "Aktivity",
                    Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                    style = Typography.AppTitle
                )
            }
        },
        bodyContent = {
            ScrollableColumn {
                ScreenBody(nav, vm)
            }

        },
        bottomBar = {

        },
        backgroundColor = Colors.AppBackground
    )
}


@ExperimentalFocus
@ExperimentalFoundationApi
@Composable
fun ScreenBody(nav: NavController, vm: TrackedActivityViewModel) {
    Column {
        ActivitySettings(vm)

        RecentActivity(nav, vm)

        RecentRecords(vm)

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivitySettings(vm: TrackedActivityViewModel){
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {

        val activity by vm.activity.observeAsState(TrackedActivity.empty)

        Column(Modifier.padding(8.dp).fillMaxWidth()) {
            ActivityName(activity)

            if (activity.type != TrackedActivity.Type.COMPLETED){
                Row(Modifier.padding(top = 8.dp)) {
                    Goal(activity)

                    ViewRange(activity)
                    Remainder("po - pa 18:00")
                }
            }
        }
    }
}

@Composable
private fun ActivityName(activity: TrackedActivity) {
    val display = remember { mutableStateOf(false) }

    DialogInputText(
        display = display,
        text = activity.name,
        title = "Zadejte název aktivity",
        onTextSet = {
            display.value = false
            GlobalScope.launch {RepositoryTrackedActivity().update(activity.copy(name = it)) }
        }
    )

    Stack(
        modifier = Modifier.background(Colors.ChipGray, shape = RoundedCornerShape(50))
            .height(30.dp).clickable(
                onClick = { display.value = true},
                indication = RippleIndication()
            ),
        alignment = Alignment.Center
    ) {

        if (activity.name.isEmpty()) {
            Text(
                text = "label",
            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxWidth(),
            text = activity.name,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

    }
}

@Composable
inline fun Goal(activity: TrackedActivity){
    val display = remember { mutableStateOf(false) }

    DialogGoal(display = display, activity = activity){
        AppDatabase.activityRep.update(activity.copy(expected = it))
    }

    Row(
        modifier = Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(onClick = {display.value = true})


    ) {
        Icon(Icons.Filled.Flag, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))
        Log.e("GOAL", "COMPOSE")

        Text(
            activity.formatGoal(),
            Modifier.weight(1f).align(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )
        )
    }

}

@Composable
private fun ViewRange(activity: TrackedActivity){
    val display = remember { mutableStateOf(false) }

    DialogTimeRange(display = display, activity.metric_range){
        if (activity.metric_range != it) GlobalScope.launch { 
            AppDatabase.activityRep.update(activity.copy(metric_range = it))
        }
    }

    Row(
        modifier = Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(onClick = {display.value = true})
    ) {
        Icon(Icons.Filled.DateRange, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            stringResource(id = activity.metric_range.label),
            Modifier.weight(1f).align(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )

        )
    }
}

@Composable
private fun Remainder(label: String){
    Row(Modifier.size(80.dp, 30.dp).padding(end = 8.dp).background(Colors.ChipGray, RoundedCornerShape(50))) {
        Icon(Icons.Filled.Timer, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            label,
            Modifier.weight(1f).align(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )

        )
    }
}

@Composable
private fun RecentActivity(nav: NavController, vm: TrackedActivityViewModel){
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {

        Column(Modifier.padding(8.dp).background(Color.White)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Recent Activity",
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp
                    ),
                )
                TextButton(onClick = {nav.navigate(R.id.action_activity_fragment_to_fragmentTrackedActivityRecords)}) {
                    Text(text = "Browse")
                }

            }

            val today = vm.metricToday.observeAsState("-")
            val week = vm.metricWeek.observeAsState("-")
            val month = vm.metricMonth.observeAsState("-")
            val days30 = vm.metric30Days.observeAsState("-")

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledMetricBlock(metric = today.value, label = "Today", color = Colors.AppAccent, width = 80.dp )
                LabeledMetricBlock(metric = week.value, label = "Week", color = Colors.AppAccent, width = 80.dp )
                LabeledMetricBlock(metric = month.value, label = "Month", color = Colors.AppAccent, width = 80.dp )
                LabeledMetricBlock(metric = days30.value, label = "30 days", color = Colors.AppAccent, width = 80.dp )
            }

            Divider(Modifier.padding(8.dp))

            val recent = vm.recent.observeAsState(listOf())

            ScrollableColumn(modifier = Modifier.height(300.dp),) {
                RecentActivityGrid(recent.value)
            }

            Divider(Modifier.padding(8.dp))

            val months = vm.months.observeAsState(listOf())

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6){
                    MetricBlock(months.value.getOrNull(it) ?: BaseMetricData.getEmpty() )
                }
            }

        }
    }
}



@Composable
private fun RecentRecords(vm: TrackedActivityViewModel){
    val items = vm.recentRecords.observeAsState(listOf())

    LazyColumnFor(
        items = items.value,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        TrackedActivityRecord(item = it )
    }
}


