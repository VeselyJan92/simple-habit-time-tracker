package com.imfibit.activitytracker.ui.screens.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.*
import com.imfibit.activitytracker.ui.screens.activity_list.ActionButton
import kotlinx.coroutines.*
import me.bytebeats.views.charts.line.LineChart
import me.bytebeats.views.charts.line.LineChartData
import me.bytebeats.views.charts.line.render.yaxis.SimpleYAxisDrawer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScreenTrackedActivity(nav: NavHostController) {

    val vm = hiltViewModel<TrackedActivityViewModel>()

    val state by vm.data.collectAsState(initial = null)

    val scaffoldState = rememberScaffoldState()

    val msg = stringResource(id = R.string.confirm_delete)
    val undo = stringResource(id = R.string.undo)

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_activity)) {

                val scope = rememberCoroutineScope()

                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = {
                        scope.launch {
                            val deleted = scaffoldState.snackbarHostState.showSnackbar(msg, undo)

                            if (deleted == SnackbarResult.Dismissed){
                                state?.activity?.let {
                                    nav.popBackStack()
                                    vm.deleteActivity(it)
                                }
                            }
                        }
                    })
                )
            }

        },
        content = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                ScreenBody(nav, state, vm)

                //LineChartView(state)

                Spacer(modifier = Modifier.height(100.dp))
            }

        },

        bottomBar = {
            AppBottomNavigation(nav)
        },
        backgroundColor = Colors.AppBackground,
        scaffoldState = scaffoldState
    )
}


@ExperimentalFoundationApi
@Composable
fun ScreenBody(nav: NavController, state: TrackedActivityState?, vm: TrackedActivityViewModel) {
    if (state != null){
        Column {
            ActivitySettings(state, vm)

            if (state.activity.type == TrackedActivity.Type.TIME){
                Live(
                    activity = state.activity,
                    onActionClick = {
                        if (state.activity.isInSession()){
                            vm.commitSession(state.activity)
                        }else{
                            vm.startSession(state.activity, it)
                        }
                    },
                    onUpdate = {
                        vm.updateSession(state.activity, it)
                    },

                    onClear = {
                        vm.clearRunning(state.activity)
                    }
                )
            }

            RecentActivity(nav, state)
        }
    }


}

@Composable
fun Live(
    activity: TrackedActivity,
    onActionClick: (LocalDateTime)->Unit,
    onUpdate: (LocalDateTime)->Unit,
    onClear: ()->Unit
) {

    val start = remember(activity.inSessionSince) {
        mutableStateOf(activity.inSessionSince)
    }


    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            ActionButton(hasMetricToday = false, activity = activity, onClick = {
                val validStart = start.value?.withSecond(LocalTime.now().second) ?: LocalDateTime.now()

                if ( validStart > LocalDateTime.now())
                    return@ActionButton

                onActionClick(validStart)
            })

            Spacer(modifier = Modifier.width(8.dp))

            EditableDatetime(
                datetime = start.value ?: LocalDateTime.now(),
                onDatetimeEdit = {
                    if(activity.isInSession()){
                        onUpdate(it)
                    }else{
                        start.value = it
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))


            Timer(
                modifier = Modifier
                    .size(100.dp, 30.dp),
                startTime = start.value,
                enable = activity.inSessionSince != null
            )

            Spacer(modifier = Modifier.width(8.dp))


            if (activity.isInSession()){
                Box(
                    modifier = Modifier
                        .size(50.dp, 30.dp)
                        .padding(end = 8.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50))
                        .clickable(onClick = onClear),
                    contentAlignment = Alignment.Center
                ){
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null )
                }
            }

        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivitySettings(state: TrackedActivityState?, vm: TrackedActivityViewModel) {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp)
    ) {

        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()) {
            ActivityName(vm, state?.activity)

            Row(Modifier.padding(top = 8.dp)) {
                Goal(vm, state?.activity)
                Spacer(modifier = Modifier.width(8.dp))

                ViewRange(vm, state?.activity)
                Spacer(modifier = Modifier.width(8.dp))

                SetTimer(state, vm)
                Spacer(modifier = Modifier.width(8.dp))

                Group(vm, state?.groups, state?.activity)
            }

        }
    }
}

@Composable
private fun ActivityName(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogInputText(
            display = display,
            text = activity.name,
            title = stringResource(id = R.string.track_activity_name),
            onTextSet = {
                vm.updateName(it)
                display.value = false
            }
    )

    TextBox(text = activity?.name ?: "-", modifier = Modifier.fillMaxWidth()){
        display.value = true
    }

}

@Composable
private inline fun Goal(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogGoal(display = display, activity = activity) {
        vm.updateGoal(TrackedActivityGoal(it, activity.goal.range))
    }

    IconTextButton(Icons.Filled.Flag, activity?.formatGoal()) {
        if (activity == null)
            return@IconTextButton

        if ( activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED)
            display.value = true
    }

}

@Composable
private fun ViewRange(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    // TODO REDO
    if (activity != null) DialogTimeRange(display = display, activity.goal.range) {
        GlobalScope.launch {
            val value = if (activity.type == TrackedActivity.Type.CHECKED && it == TimeRange.DAILY)
                1L
            else
                activity.goal.value

            vm.updateGoal(TrackedActivityGoal(value, it))
        }
    }

    IconTextButton(Icons.Filled.DateRange, activity?.goal?.range?.label?.let { stringResource(id = it) }) {
        display.value = true
    }

}

@Composable
private fun SetTimer(state: TrackedActivityState?, vm: TrackedActivityViewModel) {

    val display = remember { mutableStateOf(false) }

    if (state?.activity != null && state.activity.type == TrackedActivity.Type.TIME){
        DialogTimers(
            activity = state.activity,
            timers = state.timers,
            display = display,
            onTimerAdd = { timer -> vm.addTimer(timer)},
            onTimersReorganized = { items -> vm.reorganizeTimers(items) },
            onTimerDelete = { timer -> vm.deleteTimer(timer) },
            runTimer = { timer -> vm.scheduleTimer(timer)  ; display.value = false  }
        )

        IconTextButton(Icons.Filled.Timer, stringResource(id = R.string.scree_activity_timers)) {
            display.value = true
        }
    }
}


@Composable
private fun Group(vm: TrackedActivityViewModel, groups: List<TrackerActivityGroup>?, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null && groups != null) DialogActivityGroupPicker(display = display, activity, groups) {
        vm.setGroup(it)
        display.value = false
    }

    IconTextButton(Icons.Default.Topic, stringResource(id = R.string.activity_screen_recent_group)) {
        display.value = true
    }

}




@Composable
private fun RecentActivity(nav: NavController, state: TrackedActivityState?) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(5.dp)) {

        Column(
            Modifier
                .padding(8.dp)
                .background(Color.White)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.activity_screen_recent_activity),
                    style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 20.sp
                    ),
                )

                Spacer(Modifier.weight(1f))

                TextButton(
                    onClick = { if (state != null)
                        nav.navigate("screen_activity_history/${state.activity.id}") }
                ) {
                    Text(text = stringResource(id = R.string.screen_title_record_history))
                }
            }


            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                LabeledMetricBlock(
                    metric = state?.metricToday?.invoke() ?: "-",
                    label = stringResource(id = R.string.today),
                    color = Colors.AppAccent,
                    width = 80.dp,
                    metricStyle = metricTextStyle.copy(fontWeight = FontWeight.Bold)
                )
                LabeledMetricBlock(
                    metric = state?.metricWeek?.invoke() ?: "-",
                    label = stringResource(id = R.string.week),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
                LabeledMetricBlock(
                    metric = state?.metricMonth?.invoke() ?: "-",
                    label = stringResource(id = R.string.month),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
                LabeledMetricBlock(
                    metric = state?.metric30Days?.invoke() ?: "-",
                    label = stringResource(id = R.string.days30),
                    color = Colors.AppAccent,
                    width = 80.dp
                )
            }

            Divider(Modifier.padding(8.dp))

            if (state != null)
                RecentActivityGrid(state.activity, state.recent, nav)

            Divider(Modifier.padding(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6) {
                    MetricBlock(state?.months?.getOrNull(it) ?: MetricWidgetData({ "-" }, Colors.ChipGray, { "" }))
                }
            }

        }
    }
}

@Composable
fun RecentActivityGrid(activity: TrackedActivity, months: List<RepositoryTrackedActivity.Month>, nav: NavController){

    Column(Modifier.fillMaxWidth()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            DayOfWeek.values().forEach {
                Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center){
                    Text(
                        text = it.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).toUpperCase(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center){
                Icon(imageVector = Icons.Filled.Functions, contentDescription = null)
            }

        }

        for(month in months){
            val modifier = if (month.month.month == LocalDate.now().month) Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(5.dp)) else Modifier
            Month(modifier, activity, month, nav)
        }

    }

}


@Composable
fun LineChartView(state: TrackedActivityState?) {

    if (state == null)
        return




    Surface(elevation = 2.dp, shape = RoundedCornerShape(2.dp), modifier = Modifier.padding(8.dp).height(250.dp)) {
        Column(Modifier.padding(8.dp)) {
            LineChart(
                lineChartData = LineChartData(
                    points =  state.graph.mapIndexed { index, item ->  LineChartData.Point(item.total.toFloat()/(60*60), if (index%4 == 0) item.from.month.name else "") }
                ,
                ),
                // Optional properties.
                modifier = Modifier.fillMaxSize(),
                yAxisDrawer = SimpleYAxisDrawer(),
            )
        }
    }


}


