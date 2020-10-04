package com.janvesely.activitytracker.ui.screens.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.embedable.TrackedActivityGoal
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.*
import com.janvesely.activitytracker.ui.components.Colors
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

    val activity by vm.activity.observeAsState(null)

    Scaffold(
        topBar = {
            TrackerTopAppBar("Activity") {
                Icon(
                    asset = Icons.Default.Delete,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = {
                        nav.popBackStack()
                        GlobalScope.launch {
                            activity?.id?.let {
                                AppDatabase.activityRep.activityDAO.deleteById(it)
                            }
                        }
                    })
                )
            }

        },
        bodyContent = {
            ScrollableColumn {
                ScreenBody(nav, vm, activity)
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
fun ScreenBody(nav: NavController, vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    Column {
        ActivitySettings(activity)

        RecentActivity(nav, vm)

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitySettings(activity: TrackedActivity?){
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {


        Column(Modifier.padding(8.dp).fillMaxWidth()) {
                ActivityName(activity)

                Row(Modifier.padding(top = 8.dp)) {
                    Goal(activity)

                    ViewRange(activity)
                    Remainder("po - pa 18:00")
                }

        }
    }
}

@Composable
private fun ActivityName(activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity!= null) DialogInputText(
        display = display,
        text = activity.name,
        title = "Zadejte název aktivity",
        onTextSet = {
            GlobalScope.launch {
                val item = activity.copy(name = it)
                AppDatabase.activityRep.update(item)
            }

            display.value = false
        }
    )

    Box(
        modifier = Modifier
            .background(Colors.ChipGray, shape = RoundedCornerShape(50))
            .height(30.dp)
            .clickable(
                onClick = { display.value = true},
                indication = RippleIndication()
            ),
        alignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxWidth(),
            text = activity?.name ?: "",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

    }
}

@Composable
inline fun Goal(activity: TrackedActivity?){
    val display = remember { mutableStateOf(false) }

    if (activity!= null) DialogGoal(display = display, activity = activity){
        AppDatabase.activityRep.update(activity.copy(goal = TrackedActivityGoal(it, activity.goal.range)))
    }

    Row(
        modifier = Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(
                onClick = {
                    if (activity == null)
                        return@clickable

                    if (activity.goal.range != TimeRange.DAILY) display.value = true
                }
            )


    ) {

        Icon(Icons.Filled.Flag, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            activity?.formatGoal() ?: "-",
            Modifier.weight(1f).align(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )
        )
    }

}

@Composable
private fun ViewRange(activity: TrackedActivity?){
    val display = remember { mutableStateOf(false) }

    if (activity!= null) DialogTimeRange(display = display, activity.goal.range){
        GlobalScope.launch {
            val value = if (activity.type == TrackedActivity.Type.CHECKED && it == TimeRange.DAILY)
                1L
            else
                activity.goal.value

            val item = activity.copy(goal = TrackedActivityGoal(value, it))

            AppDatabase.activityRep.update(item)
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
            activity?.goal?.range?.label?.let { stringResource(id = it) } ?: "-",
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
                TextButton(onClick = {}) {
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

            RecentActivityGrid(recent.value, nav)


            Divider(Modifier.padding(8.dp))

            val months = vm.months.observeAsState(listOf())

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6){
                    MetricBlock(months.value.getOrNull(it) ?: MetricWidgetData.Labeled({"-"}, {""}, Colors.ChipGray ) )
                }
            }

        }
    }
}


