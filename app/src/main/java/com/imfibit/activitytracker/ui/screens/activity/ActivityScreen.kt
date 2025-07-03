package com.imfibit.activitytracker.ui.screens.activity

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.dialogs.*
import com.imfibit.activitytracker.ui.components.util.TestableContent
import com.imfibit.activitytracker.ui.screens.activity_list.ActionButton
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.DEFAULT
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.IN_SESSION
import com.imfibit.activitytracker.ui.viewmodels.RecordNavigatorImpl
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ScreenTrackedActivity(
    nav: NavHostController,
) {
    val recordVM = hiltViewModel<RecordViewModel>()
    val vm = hiltViewModel<TrackedActivityViewModel>()

    val name = vm.activityName.value

    ScreenTrackedActivity(
        nav = nav,
        vm = vm,
        activityName = name,
        activityState = vm.data,
        onDayClicked = { activity, date -> RecordNavigatorImpl.onDayClicked(nav, activity, date) },
        onDayLongClicked = { activity, date ->
            RecordNavigatorImpl.onDaylongClicked(
                nav,
                recordVM,
                activity,
                date
            )
        },
        onDeleteActivity = vm::deleteActivity,
        onActivityNameUpdate = vm::refreshName,
        onNavigateToHistory = { nav.navigate(Destinations.ScreenActivityHistory(it.id)) },
        onNavigateBack = { nav.popBackStack() }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenTrackedActivity(
    nav: NavHostController,
    vm: TrackedActivityViewModel,
    activityName: String?,
    activityState: Flow<TrackedActivityState?>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
    onDeleteActivity: (TrackedActivity) -> Unit,
    onActivityNameUpdate: (String) -> Unit,
    onNavigateToHistory: (TrackedActivity) -> Unit,
    onNavigateBack: () -> Unit,
) = TestableContent(testTag = TestTag.TRACKED_ACTIVITY_SCREEN) {

    val state by activityState.collectAsState(initial = null)

    val msg = stringResource(id = R.string.confirm_delete)

    CheckNotificationPermission()

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TopBarBackButton(onBack = onNavigateBack)

                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestTag.TRACKED_ACTIVITY_EDIT_NAME),
                    value = activityName ?: "",
                    singleLine = true,
                    onValueChange = onActivityNameUpdate,
                    textStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 25.sp)
                )

                val dialogDelete = remember {
                    mutableStateOf(false)
                }

                DialogAgree(
                    display = dialogDelete,
                    title = msg,
                    onAction = { delete ->
                        dialogDelete.value = false

                        val activity = state?.activity

                        if (delete && activity != null) {
                            nav.popBackStack()
                            onDeleteActivity(activity)
                        }
                    }
                )

                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.Delete,
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(onClick = {
                            dialogDelete.value = true
                        })
                )


            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(8.dp)
            ) {
                item {
                    ActivitySettings(state, vm)
                }

                item {
                    state?.let { state ->
                        Surface(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column {

                                RecentActivity(
                                    state,
                                    onNavigateToHistory,
                                    onDayClicked,
                                    onDayLongClicked
                                )
                            }


                        }

                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

        },


        containerColor = Colors.AppBackground,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionActivityCustomStart(
    onActionClick: (LocalDateTime) -> Unit,
    onUpdate: (LocalDateTime) -> Unit,
    onClear: () -> Unit,
    state: TrackedActivityState,
    vm: TrackedActivityViewModel,
) {

    val activity = state.activity

    val start = remember(activity.inSessionSince) {
        mutableStateOf(activity.inSessionSince)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val action = if (activity.isInSession()) IN_SESSION else DEFAULT

        ActionButton(
            modifier = Modifier.testTag(TestTag.TRACKED_ACTIVITY_ACTION_BUTTON),
            actionButton = action,
            activity = activity,
            onClick = {
                val validStart = start.value ?: LocalDateTime.now()

                if (validStart >= LocalDateTime.now())
                    return@ActionButton

                onActionClick(validStart)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))


        if (!activity.isInSession()) {

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(8.dp))

            val context: Context = androidx.compose.ui.platform.LocalContext.current

            val askForExactAlarm = remember { mutableStateOf(false) }

            DialogAskForExactAlarm(askForExactAlarm)

            val display = remember { mutableStateOf(false) }

            DialogTimers(
                activity = activity,
                timers = state.timers,
                display = display,
                onTimerAdd = { timer -> vm.addTimer(timer) },
                swapTimer = vm::swapTimer,
                onTimerDelete = { timer -> vm.deleteTimer(timer) },
                runTimer = {
                    display.value = false

                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        askForExactAlarm.value = true
                    } else {
                        vm.scheduleTimer(it)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Colors.ChipGray)
                    .height(30.dp)
                    .clickable(
                        onClick = {
                            display.value = true
                        }
                    )
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddAlarm,
                    contentDescription = null,
                )

                Text(
                    text = "Start timer",
                    modifier = Modifier.padding(start = 4.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        } else {

            Text(
                text = stringResource(id = R.string.activity_in_session) + " " + activity.inSessionSince!!.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            var showPicker by remember { mutableStateOf(false) }

            if (showPicker) {
                val duration = Duration.between(activity.inSessionSince!!, LocalDateTime.now())

                val timePickerState = rememberTimePickerState(
                    initialHour = duration.toHoursPart().coerceIn(0, 23),
                    initialMinute = duration.toMinutesPart().coerceIn(0, 59),
                    is24Hour = true,
                )

                TimePickerDialog(
                    onDismissRequest = {
                        showPicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showPicker = false

                                val newStart = activity.inSessionSince
                                    ?.minusHours(timePickerState.hour.toLong())
                                    ?.minusMinutes(timePickerState.minute.toLong())
                                    ?.withSecond(LocalDateTime.now().second)
                                    ?.withNano(LocalDateTime.now().nano)

                                onUpdate(newStart ?: LocalDateTime.now())
                            }
                        ) {
                            Text("Edit duration")
                        }
                    },
                    title = {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = "Edit session length",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        )
                    }
                ) {
                    TimePicker(
                        state = timePickerState,
                    )
                }
            }

            TimerBlock(
                modifier = Modifier
                    .height(30.dp),
                startTime = start.value,
                onClick = {
                    showPicker = true
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(50.dp, 30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(10.dp))
                    .clickable(onClick = onClear),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivitySettings(state: TrackedActivityState?, vm: TrackedActivityViewModel) {

    Surface(
        modifier = Modifier
            .padding(top = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column() {
            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                // horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Goal(vm, state?.activity)
                Spacer(modifier = Modifier.width(8.dp))

                ViewRange(vm, state?.activity)
                Spacer(modifier = Modifier.width(8.dp))

                Group(vm, state?.groups, state?.activity)
                Spacer(modifier = Modifier.width(8.dp))

                if (state != null) {
                    SetChallange(state, vm)
                }
            }

            state?.let {
                if (state.activity.type == TrackedActivity.Type.TIME) {
                    SessionActivityCustomStart(
                        state = state,
                        vm = vm,
                        onActionClick = {
                            if (state.activity.isInSession()) {
                                vm.commitSession(state.activity)
                            } else {
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

            }
        }

    }


}

@Composable
private fun RowScope.Goal(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogGoal(display = display, activity = activity) {
        vm.updateGoal(TrackedActivityGoal(it, activity.goal.range))
    }

    IconTextButton(Icons.Outlined.Flag, activity?.formatGoal(), modifier = Modifier.weight(1f)) {
        if (activity == null)
            return@IconTextButton

        if (activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED)
            display.value = true
    }

}

@Composable
private fun RowScope.ViewRange(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    // TODO REDO
    if (activity != null) DialogTimeRange(display = display, activity.goal.range) {
        MainScope().launch {
            val value = if (activity.type == TrackedActivity.Type.CHECKED && it == TimeRange.DAILY)
                1L
            else
                activity.goal.value

            vm.updateGoal(TrackedActivityGoal(value, it))
        }
    }

    IconTextButton(
        Icons.Filled.DateRange,
        activity?.goal?.range?.label?.let { stringResource(id = it) },
        modifier = Modifier.weight(1f)
    ) {
        display.value = true
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.SetChallange(state: TrackedActivityState, vm: TrackedActivityViewModel) {

    var display by remember { mutableStateOf(false) }

    if (display){
        BottomSheetProgressGoal(
            activity = state.activity,
            getLiveMetric = { from, to -> vm.getChallengeMetric(from, to) },
            onSet = { vm.updateActivity(state.activity.copy(challenge = it)) },
            onDelete = { vm.updateActivity(state.activity.copy(challenge = TrackedActivityChallenge.empty)) },
            onDismissRequest = {
                display = false
            }
        )
    }

    IconTextButton(Icons.Filled.Flag, "Challenge", modifier = Modifier.weight(1f)) {
        display = true
    }

}


@Composable
private fun RowScope.Group(
    vm: TrackedActivityViewModel,
    groups: List<TrackerActivityGroup>?,
    activity: TrackedActivity?,
) {
    val display = remember { mutableStateOf(false) }

    if (activity != null && groups != null) DialogActivityGroupPicker(
        display = display,
        activity,
        groups
    ) {
        vm.setGroup(it)
        display.value = false
    }

    IconTextButton(
        Icons.Default.Topic,
        stringResource(id = R.string.activity_screen_recent_group),
        modifier = Modifier.weight(1f)
    ) {
        display.value = true
    }

}


@Composable
private fun RecentActivity(
    state: TrackedActivityState?,
    onNavigateToHistory: (TrackedActivity) -> Unit,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .fillMaxHeight()
        ) {

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
                    onClick = {
                        if (state != null) {
                            onNavigateToHistory(state.activity)
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.screen_title_record_history))
                }
            }

            if (state != null && state.activity.challenge.isSet()) {
                GoalProgressBar(
                    challenge = state.activity.challenge,
                    actual = state.challengeMetric,
                    type = state.activity.type
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.activity.isGoalSet()) {
                    val aheadInDays = state.activity.getChallengeAheadDays(state.challengeMetric)

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = stringResource(
                            R.string.ahead_of_challenge_deadline_note,
                            aheadInDays
                        ),
                        style = TextStyle(
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    )
                }
            }



            if (state != null)
                RecentActivityGrid(state.activity, state.recent, onDayClicked, onDayLongClicked)

            HorizontalDivider(Modifier.padding(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6) {
                    MetricBlock(
                        state?.months?.getOrNull(it) ?: MetricWidgetData(
                            { "-" },
                            Colors.ChipGray,
                            { "" })
                    )
                }
            }

        }
    }
}

@Composable
fun RecentActivityGrid(
    activity: TrackedActivity,
    months: List<RepositoryTrackedActivity.Month>,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
) {

    Column(Modifier.fillMaxWidth()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            DayOfWeek.values().forEach {
                Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = it.getDisplayName(
                            java.time.format.TextStyle.SHORT,
                            Locale.getDefault()
                        ).uppercase(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.Functions, contentDescription = null)
            }

        }

        for (month in months) {
            val modifier = if (month.month.month == LocalDate.now().month) Modifier.background(
                Color(0xFFF5F5F5),
                RoundedCornerShape(5.dp)
            ) else Modifier
            TrackedActivityMonth(modifier, activity, month, onDayClicked, onDayLongClicked)
        }

    }

}

