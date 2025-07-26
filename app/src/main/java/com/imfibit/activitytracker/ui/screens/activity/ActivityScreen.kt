package com.imfibit.activitytracker.ui.screens.activity

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.TimerBlock
import com.imfibit.activitytracker.ui.components.TopBarBackButton
import com.imfibit.activitytracker.ui.components.TrackedActivityMonth
import com.imfibit.activitytracker.ui.components.dialogs.BottomSheetProgressGoal
import com.imfibit.activitytracker.ui.components.dialogs.CheckNotificationPermission
import com.imfibit.activitytracker.ui.components.dialogs.DialogActivityGroupPicker
import com.imfibit.activitytracker.ui.components.dialogs.DialogAgree
import com.imfibit.activitytracker.ui.components.dialogs.DialogAskForExactAlarm
import com.imfibit.activitytracker.ui.components.dialogs.DialogGoal
import com.imfibit.activitytracker.ui.components.dialogs.DialogTimeRange
import com.imfibit.activitytracker.ui.components.dialogs.DialogTimers
import com.imfibit.activitytracker.ui.components.util.TestableContent
import com.imfibit.activitytracker.ui.screens.activity_history.BottomSheetActivityHistory
import com.imfibit.activitytracker.ui.screens.activity_list.ActionButton
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.DEFAULT
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.IN_SESSION
import com.imfibit.activitytracker.ui.viewmodels.RecordNavigatorImpl
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import com.imfibit.activitytracker.ui.components.GoalProgressBar
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Preview
@Composable
fun ScreenTrackedActivity_Preview() = AppTheme {
    ScreenTrackedActivity(
        state = TrackedActivityState(
            activity = DevSeeder.getTrackedActivityTime(
            ),
            timers = listOf(),
            recent = listOf(
                DevSeeder.getMonthData(YearMonth.now().minusMonths(1)),
                DevSeeder.getMonthData(YearMonth.now())
            ),
            months = listOf(),
            groups = listOf(),
            challengeMetric = 1,
        ),
        onDayClicked = { _, _ -> },
        onDayLongClicked = { _, _ -> },
        onDeleteActivity = {},
        onNameChanged = {},
        onNavigateToHistory = {},
        onNavigateBack = {},
        scheduleTimer = {},
        deleteTimer = {},
        addTimer = {},
        swapTimer = { _, _ -> },
        getChallengeMetric = { _, _ -> 0L },
        updateActivity = {},
        onSetGroup = {},
        updateGoal = {},
        commitSession = {},
        startSession = {},
        updateSession = { _, _ -> },
        clearRunning = {}
    )
}

@Composable
fun ScreenTrackedActivity(
    nav: NavHostController,
) {
    val recordVM = hiltViewModel<RecordViewModel>()
    val vm = hiltViewModel<TrackedActivityViewModel>()

    var showHistoryBottomSheet by remember { mutableStateOf(false) }

    val data by vm.data.collectAsStateWithLifecycle()

    if (showHistoryBottomSheet) {
        BottomSheetActivityHistory(
            onDismissRequest = { showHistoryBottomSheet = false },
            activity = data?.activity,
            months = vm.months,
            nav = nav,
        )
    }

    CheckNotificationPermission()

    val haptics = LocalHapticFeedback.current

    data?.let {
        ScreenTrackedActivity(
            state = it,
            onDayClicked = { activity, date ->
                RecordNavigatorImpl.onDayClicked(
                    nav,
                    activity,
                    date
                )
            },
            onDayLongClicked = { activity, date ->
                RecordNavigatorImpl.onDaylongClicked(
                    nav = nav,
                    recordViewModel = recordVM,
                    activity = activity,
                    date = date,
                    haptic = haptics
                )
            },
            onDeleteActivity = vm::deleteActivity,
            onNameChanged = vm::updateName,
            onNavigateToHistory = {
                showHistoryBottomSheet = true
            },
            onNavigateBack = { nav.popBackStack() },
            scheduleTimer = vm::scheduleTimer,
            deleteTimer = vm::deleteTimer,
            addTimer = vm::addTimer,
            swapTimer = vm::swapTimer,
            getChallengeMetric = vm::getChallengeMetric,
            updateActivity = vm::updateActivity,
            onSetGroup = vm::setGroup,
            updateGoal = vm::updateGoal,
            commitSession = vm::commitSession,
            startSession = { vm.startSession(it, LocalDateTime.now()) },
            updateSession = vm::updateSession,
            clearRunning = vm::clearRunning
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenTrackedActivity(
    state: TrackedActivityState,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit,
    onDeleteActivity: (TrackedActivity) -> Unit,
    onNameChanged: (String) -> Unit,
    onNavigateToHistory: (TrackedActivity) -> Unit,
    onNavigateBack: () -> Unit,
    scheduleTimer: (PresetTimer) -> Unit,
    deleteTimer: (PresetTimer) -> Unit,
    addTimer: (PresetTimer) -> Unit,
    swapTimer: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    getChallengeMetric: suspend (LocalDate?, LocalDate?) -> Long,
    updateActivity: (TrackedActivity) -> Unit,
    onSetGroup: (TrackerActivityGroup?) -> Unit,
    updateGoal: (TrackedActivityGoal) -> Unit,
    commitSession: (TrackedActivity) -> Unit,
    startSession: (TrackedActivity) -> Unit,
    updateSession: (TrackedActivity, LocalDateTime) -> Unit,
    clearRunning: (TrackedActivity) -> Unit,
) = TestableContent(testTag = TestTag.TRACKED_ACTIVITY_SCREEN) {

    val msg = stringResource(id = R.string.confirm_delete)

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TopBarBackButton(onBack = onNavigateBack)

                var name by remember(state.activity.name) {
                    mutableStateOf(state.activity.name)
                }

                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestTag.TRACKED_ACTIVITY_EDIT_NAME),
                    value = name,
                    singleLine = true,
                    onValueChange = {
                        name = it
                        onNameChanged(it)
                    },
                    textStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 25.sp)
                )

                var dialogDelete by remember { mutableStateOf(false) }
                if (dialogDelete) {
                    DialogAgree(
                        onDismissRequest = { dialogDelete = false },
                        title = msg,
                        onAction = { delete ->
                            dialogDelete = false

                            val activity = state.activity

                            if (delete) {
                                onNavigateBack()
                                onDeleteActivity(activity)
                            }
                        }
                    )
                }

                IconButton(
                    onClick = {
                        dialogDelete = true
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 8.dp)
            ) {
                item {
                    ActivitySettings(
                        state = state,
                        scheduleTimer = scheduleTimer,
                        deleteTimer = deleteTimer,
                        addTimer = addTimer,
                        swapTimer = swapTimer,
                        getChallengeMetric = getChallengeMetric,
                        updateActivity = updateActivity,
                        onSetGroup = onSetGroup,
                        updateGoal = updateGoal,
                        commitSession = commitSession,
                        startSession = startSession,
                        updateSession = updateSession,
                        clearRunning = clearRunning
                    )
                }

                item {
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
fun StartSession(
    state: TrackedActivityState,
    onActionClick: (LocalDateTime) -> Unit,
    onUpdate: (LocalDateTime) -> Unit,
    onClear: () -> Unit,
    swapTimer: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    addTimer: (PresetTimer) -> Unit,
    deleteTimer: (PresetTimer) -> Unit,
    scheduleTimer: (PresetTimer) -> Unit,
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

        val haptics = LocalHapticFeedback.current

        ActionButton(
            modifier = Modifier.testTag(TestTag.TRACKED_ACTIVITY_ACTION_BUTTON),
            actionButton = action,
            activity = activity,
            onClick = {
                val validStart = start.value ?: LocalDateTime.now()

                if (validStart >= LocalDateTime.now())
                    return@ActionButton

                haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                onActionClick(validStart)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (!activity.isInSession()) {
            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(8.dp))

            val context: Context = LocalContext.current

            var askForExactAlarm by remember { mutableStateOf(false) }
            if (askForExactAlarm) {
                DialogAskForExactAlarm(
                    onDismissRequest = {
                        askForExactAlarm
                    }
                )
            }


            var display by remember { mutableStateOf(false) }
            if (display) {
                DialogTimers(
                    onDismissRequest = { display = false },
                    activity = activity,
                    timers = state.timers,
                    onTimerAdd = addTimer,
                    swapTimer = swapTimer,
                    onTimerDelete = deleteTimer,
                    runTimer = {
                        display = false

                        val alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            askForExactAlarm = true
                        } else {
                            scheduleTimer(it)
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Colors.ChipGray)
                    .height(30.dp)
                    .clickable(
                        onClick = {
                            display = true
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
private fun ActivitySettings(
    state: TrackedActivityState,
    scheduleTimer: (PresetTimer) -> Unit,
    deleteTimer: (PresetTimer) -> Unit,
    addTimer: (PresetTimer) -> Unit,
    swapTimer: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    getChallengeMetric: suspend (LocalDate?, LocalDate?) -> Long,
    updateActivity: (TrackedActivity) -> Unit,
    onSetGroup: (TrackerActivityGroup?) -> Unit,
    updateGoal: (TrackedActivityGoal) -> Unit,
    commitSession: (TrackedActivity) -> Unit,
    startSession: (TrackedActivity) -> Unit,
    updateSession: (TrackedActivity, LocalDateTime) -> Unit,
    clearRunning: (TrackedActivity) -> Unit,
) {
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Goal(
                    activity = state.activity,
                    updateGoal = updateGoal
                )

                ViewRange(
                    activity = state.activity,
                    updateGoal = updateGoal
                )

                Group(
                    groups = state.groups,
                    activity = state.activity,
                    onSetGroup = onSetGroup
                )

                SetChallange(
                    state = state,
                    getChallengeMetric = getChallengeMetric,
                    updateActivity = updateActivity
                )
            }

            if (state.activity.type == TrackedActivity.Type.TIME) {
                StartSession(
                    onActionClick = {
                        if (state.activity.isInSession()) {
                            commitSession(state.activity)
                        } else {
                            startSession(state.activity)
                        }
                    },
                    onUpdate = {
                        updateSession(state.activity, it)
                    },
                    onClear = {
                        clearRunning(state.activity)
                    },
                    state = state,
                    swapTimer = swapTimer,
                    addTimer = addTimer,
                    deleteTimer = deleteTimer,
                    scheduleTimer = scheduleTimer
                )
            }
        }
    }
}

@Composable
private fun RowScope.Goal(
    activity: TrackedActivity,
    updateGoal: (TrackedActivityGoal) -> Unit,
) {
    var display by remember { mutableStateOf(false) }
    if (display) {
        DialogGoal(
            onDismissRequest = { display = false },
            activity = activity,
            onGoalSet = {
                updateGoal(TrackedActivityGoal(it, activity.goal.range))
            }
        )
    }

    HeaderButton(
        icon = Icons.Outlined.Flag,
        text = activity.formatGoal(),
        modifier = Modifier.weight(1f),
        onClick = {
            if (activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED) {
                display = true
            }
        }
    )
}

@Composable
private fun RowScope.ViewRange(
    activity: TrackedActivity,
    updateGoal: (TrackedActivityGoal) -> Unit,
) {
    var display by remember { mutableStateOf(false) }
    if (display) {
        DialogTimeRange(
            onDismissRequest = { display = false },
            range = activity.goal.range
        ) {
            val value =
                if (activity.type == TrackedActivity.Type.CHECKED && it == TimeRange.DAILY)
                    1L
                else
                    activity.goal.value

            updateGoal(TrackedActivityGoal(value, it))

        }
    }

    HeaderButton(
        Icons.Filled.DateRange,
        activity.goal.range.label.let { stringResource(id = it) },
        modifier = Modifier.weight(1f)
    ) {
        display = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.SetChallange(
    state: TrackedActivityState,
    getChallengeMetric: suspend (LocalDate?, LocalDate?) -> Long,
    updateActivity: (TrackedActivity) -> Unit,
) {
    var display by remember { mutableStateOf(false) }
    if (display) {
        BottomSheetProgressGoal(
            activity = state.activity,
            getLiveMetric = getChallengeMetric,
            onSet = { updateActivity(state.activity.copy(challenge = it)) },
            onDelete = { updateActivity(state.activity.copy(challenge = TrackedActivityChallenge.empty)) },
            onDismissRequest = {
                display = false
            }
        )
    }

    HeaderButton(
        icon = null,
        text = "Challenge",
        modifier = Modifier.weight(1f),
        onClick = {
            display = true
        }
    )


}


@Composable
private fun RowScope.Group(
    groups: List<TrackerActivityGroup>,
    activity: TrackedActivity,
    onSetGroup: (TrackerActivityGroup?) -> Unit,
) {
    var display by remember { mutableStateOf(false) }
    if (display) {
        DialogActivityGroupPicker(
            onDismissRequest = { display = false },
            activity = activity,
            groups = groups,
            select = {
                onSetGroup(it)
                display = false
            }
        )
    }

    HeaderButton(
        icon = Icons.Default.Topic,
        text = stringResource(id = R.string.activity_screen_recent_group),
        modifier = Modifier.weight(1f),
        onClick = {
            display = true
        }
    )
}

@Composable
private fun RecentActivity(
    state: TrackedActivityState,
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        onNavigateToHistory(state.activity)
                    }
                ) {
                    Text(text = stringResource(id = R.string.screen_title_record_history))
                }
            }

            if (state.activity.challenge.isSet()) {
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

            RecentActivityGrid(state.activity, state.recent, onDayClicked, onDayLongClicked)

            HorizontalDivider(Modifier.padding(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(6) {
                    MetricBlock(
                        state.months.getOrNull(it) ?: MetricWidgetData(
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
            DayOfWeek.entries.forEach {
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

            Spacer(Modifier.size(48.dp, 30.dp))
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

@Composable
private fun HeaderButton(
    icon: ImageVector?,
    text: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.Black
        ),
        onClick = onClick,
        modifier = modifier
            .height(30.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }


            if (text == null || icon == null) {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                modifier = modifier.weight(1f),
                text = text ?: "-",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

