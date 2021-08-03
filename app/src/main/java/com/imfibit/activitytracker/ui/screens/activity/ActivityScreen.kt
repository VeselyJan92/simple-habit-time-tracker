package com.imfibit.activitytracker.ui.screens.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
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
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.AppBottomNavigation
import kotlinx.coroutines.*


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScreenTrackedActivity(nav: NavHostController, activityId: Long) {

    val vm = viewModel<TrackedActivityViewModel>(factory = TrackedActivityVMFactory(activityId))

    val state by vm.screenState.observeAsState(null)

    val scaffoldState = rememberScaffoldState()

    val msg = stringResource(id = R.string.confirm_delete)
    val undo = stringResource(id = R.string.undo)

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_activity)) {
                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = {
                        GlobalScope.launch {

                            val deleted = scaffoldState.snackbarHostState.showSnackbar(msg, undo)

                            if (deleted == SnackbarResult.Dismissed){
                                state?.activity?.id?.let {
                                    AppDatabase.activityRep.activityDAO.deleteById(it)
                                }

                                withContext(Dispatchers.Main) {
                                    nav.popBackStack()
                                }
                            }

                        }
                    })
                )
            }

        },
        content = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                ScreenBody(nav, state)

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
fun ScreenBody(nav: NavController, state: TrackedActivityState?) {
    Column {
        ActivitySettings(state)

        RecentActivity(nav, state)

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivitySettings(state: TrackedActivityState?) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {


        Column(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()) {
            ActivityName(state?.activity)

            Row(Modifier.padding(top = 8.dp)) {
                Goal(state?.activity)

                ViewRange(state?.activity)

                Priority(state?.activity)

                //Remainder("po - pa 18:00")
            }

        }
    }
}

@Composable
private fun ActivityName(activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogInputText(
            display = display,
            text = activity.name,
            title = stringResource(id = R.string.track_activity_name),
            onTextSet = {
                GlobalScope.launch {
                    val item = activity.copy(name = it)
                    AppDatabase.activityRep.update(item)
                }

                display.value = false
            }
    )

    val source = remember { MutableInteractionSource() }

    Box(
            modifier = Modifier
                .background(Colors.ChipGray, shape = RoundedCornerShape(50))
                .height(30.dp)
                .clickable(
                    onClick = { display.value = true },
                    indication = rememberRipple(),
                    interactionSource = source
                ),
            contentAlignment = Alignment.Center
    ) {
        Text(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                text = activity?.name ?: "",
                style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                )
        )

    }
}

@Composable
private inline fun Goal(activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogGoal(display = display, activity = activity) {
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

                    if (activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED) display.value =
                        true
                }
            )


    ) {

        Icon(Icons.Filled.Flag,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
                .size(15.dp),

            contentDescription = null
        )

        Text(
            activity?.formatGoal() ?: "-",
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                    fontSize = 10.sp
            )
        )
    }

}

@Composable
private fun ViewRange(activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogTimeRange(display = display, activity.goal.range) {
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
            .clickable(onClick = { display.value = true })
    ) {
        Icon(Icons.Filled.DateRange,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
                .size(15.dp))

        Text(
            activity?.goal?.range?.label?.let { stringResource(id = it) } ?: "-",
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                    fontSize = 10.sp
            )

        )
    }
}

@Composable
private fun Priority(activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogTempPriority(display = display, activity = activity)

    Row(
        modifier = Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(
                onClick = { display.value = true }
            )
    ) {

        Icon(Icons.Filled.UnfoldMore,
            contentDescription = null,
            Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
                .size(15.dp))

        Text(
                activity?.position?.toString() ?: "-",
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                        fontSize = 10.sp
                )
        )
    }

}

@Composable
private fun Remainder(label: String) {
    Row(
        Modifier
            .size(80.dp, 30.dp)
            .padding(end = 8.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))) {
        Icon(Icons.Filled.Timer,
            contentDescription = null,
            Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
                .size(15.dp))

        Text(
            label,
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                    fontSize = 10.sp
            )

        )
    }
}

@Composable
private fun RecentActivity(nav: NavController, state: TrackedActivityState?) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {

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
               /* TextButton(onClick = {}) {
                    Text(text = stringResource(id = R.string.browse))
                }*/

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

            RecentActivityGrid(state?.recent ?: listOf(), nav)

            Divider(Modifier.padding(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6) {
                    MetricBlock(state?.months?.getOrNull(it)
                            ?: MetricWidgetData.Labeled({ "-" }, { "" }, Colors.ChipGray))
                }
            }

        }
    }
}


