package com.imfibit.activitytracker.ui.screens.activity

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
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogGoal
import com.imfibit.activitytracker.ui.components.dialogs.DialogInputText
import com.imfibit.activitytracker.ui.components.dialogs.DialogTempPriority
import com.imfibit.activitytracker.ui.components.dialogs.DialogTimeRange
import com.imfibit.getitdone.database.AppDatabase
import kotlinx.coroutines.*

@ExperimentalFocus
@ExperimentalFoundationApi
@ExperimentalLayout
@Composable
fun TrackedActivityScreen(nav: NavController, vm: TrackedActivityViewModel) {

    val activity by vm.activity.observeAsState(null)

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_activity)) {
                Icon(
                    asset = Icons.Default.Delete,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = {
                        GlobalScope.launch {
                            activity?.id?.let {
                                AppDatabase.activityRep.activityDAO.deleteById(it)
                            }

                            withContext(Dispatchers.Main) {
                                nav.popBackStack()
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

                    Priority(activity)

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
        title = stringResource(id = R.string.activity_screen_enter_name),
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

                    if (activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED) display.value = true
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
fun Priority(activity: TrackedActivity?){
    val display = remember { mutableStateOf(false) }

    if (activity!= null) DialogTempPriority(display = display, activity = activity)

    Row(
        modifier = Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(
                onClick = { display.value = true }
            )
    ) {

        Icon(Icons.Filled.UnfoldMore, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            activity?.position?.toString() ?: "-",
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
                    text = stringResource(id = R.string.activity_screen_recent_activity),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp
                    ),
                )
                TextButton(onClick = {}) {
                    Text(text = stringResource(id = R.string.browse))
                }

            }

            val today = vm.metricToday.observeAsState({"-"})
            val week = vm.metricWeek.observeAsState({"-"})
            val month = vm.metricMonth.observeAsState({"-"})
            val days30 = vm.metric30Days.observeAsState({"-"})

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledMetricBlock(
                    metric = today.value.invoke(),
                    label = stringResource(id = R.string.today),
                    color = Colors.AppAccent,
                    width = 80.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold)
                )
                LabeledMetricBlock(
                    metric = week.value.invoke(),
                    label = stringResource(id = R.string.week),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
                LabeledMetricBlock(
                    metric = month.value.invoke(),
                    label = stringResource(id = R.string.month),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
                LabeledMetricBlock(
                    metric = days30.value.invoke(),
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
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


