package com.imfibit.activitytracker.ui.screens.activity

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddAlarm
import androidx.compose.material.icons.outlined.Flag
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
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.*
import com.imfibit.activitytracker.ui.screens.activity_list.ActionButton
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.DEFAULT
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.IN_SESSION
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import kotlinx.coroutines.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScreenTrackedActivity(nav: NavHostController, scaffoldState: ScaffoldState) {

    val vm = hiltViewModel<TrackedActivityViewModel>()

    val state by vm.data.collectAsState(initial = null)

    val msg = stringResource(id = R.string.confirm_delete)

    CheckNotificationPermission()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {

                TopBarBackButton(navHostController = nav)

                BasicTextField(
                    modifier = Modifier.weight(1f),
                    value = vm.activityName.value ?: "",
                    singleLine = true,
                    onValueChange = {vm.refreshName(it)},
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

                        if(delete && activity != null){
                            nav.popBackStack()
                            vm.deleteActivity(activity)
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
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)) {
                ScreenBody(nav, state, vm)

                //LineChartView(state)

                Spacer(modifier = Modifier.height(100.dp))
            }

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
                SessionActivityCustomStart(
                    state = state,
                    vm = vm,
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


            //TextRecords()
        }
    }


}

@Composable
fun TextRecords() {
    Surface(elevation = 2.dp, modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(
            Modifier
                .padding(8.dp)
                .background(Color.White)
        ) {


            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Recent logs",
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp
                ),
            )

            repeat(7){

                Row() {

                    BaseMetricBlock("17. 02", color = Colors.ChipGray, width = 60.dp)
                    
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(text = "Random record from the undergroud")
                }
                


                Divider(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth())

                

            }
        }
    }
}

@Composable
fun SessionActivityCustomStart(
    onActionClick: (LocalDateTime) -> Unit,
    onUpdate: (LocalDateTime) -> Unit,
    onClear: () -> Unit,
    state: TrackedActivityState,
    vm: TrackedActivityViewModel
) {

    val activity = state.activity

    val start = remember(activity.inSessionSince) {
        mutableStateOf(activity.inSessionSince)
    }

    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val action = if (activity.isInSession()) IN_SESSION else DEFAULT

            ActionButton(actionButton = action, activity = activity, onClick = {
                val validStart = start.value ?: LocalDateTime.now()

                if (validStart >= LocalDateTime.now())
                    return@ActionButton

                onActionClick(validStart)
            })

            Spacer(modifier = Modifier.width(8.dp))


            if(activity.isInSession()){
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
            }else{
                val context : Context = androidx.compose.ui.platform.LocalContext.current

                val askForExactAlarm = remember { mutableStateOf(false) }

                DialogAskForExactAlarm(askForExactAlarm)

                val display = remember { mutableStateOf(false) }

                DialogTimers(
                    activity = activity,
                    timers = state.timers,
                    display = display,
                    onTimerAdd = { timer -> vm.addTimer(timer)},
                    onTimersReorganized = { items -> vm.reorganizeTimers(items) },
                    onTimerDelete = { timer -> vm.deleteTimer(timer) },
                    runTimer = {
                        display.value = false

                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                            askForExactAlarm.value = true
                        }else{
                            vm.scheduleTimer(it)
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .background(Colors.ChipGray, RoundedCornerShape(50))
                        .clickable(onClick = { display.value = true }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddAlarm,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 5.dp)
                    )

                    Text(
                        text = "Start timer",
                        modifier = Modifier.padding(end = 8.dp, start = 4.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }


            }


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
        modifier = Modifier
            .padding(horizontal = 8.dp)
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

                SetChallange(state, vm)
            }
        }

    }





}

/*@Composable
private fun ActivityName(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogInputText(
            display = display,
            text = activity.name,
            title = stringResource(id = R.string.track_activity_name),
            onTextSet = {
                //vm.updateName(it)
                display.value = false
            }
    )

    TextBox(text = activity?.name ?: "-", modifier = Modifier.fillMaxWidth()){
        display.value = true
    }

}*/

@Composable
private fun RowScope.Goal(vm: TrackedActivityViewModel, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null) DialogGoal(display = display, activity = activity) {
        vm.updateGoal(TrackedActivityGoal(it, activity.goal.range))
    }

    IconTextButton(Icons.Outlined.Flag, activity?.formatGoal(), modifier = Modifier.weight(1f)) {
        if (activity == null)
            return@IconTextButton

        if ( activity.goal.range != TimeRange.DAILY || activity.type != TrackedActivity.Type.CHECKED)
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

    IconTextButton(Icons.Filled.DateRange, activity?.goal?.range?.label?.let { stringResource(id = it) }, modifier = Modifier.weight(1f)) {
        display.value = true
    }

}

@Composable
private fun RowScope.SetChallange(state: TrackedActivityState?, vm: TrackedActivityViewModel) {

    val display = remember { mutableStateOf(false) }

    DialogProgressGoal(
        display = display,
        activity = state!!.activity,
        getLiveMetric = { from, to -> vm.getChallengeMetric(from, to) },
        onSet = { vm.updateActivity(state.activity.copy(challenge = it))},
        onDelete = {vm.updateActivity(state.activity.copy(challenge = TrackedActivityChallenge.empty))}
    )

    IconTextButton(Icons.Filled.Flag, "Challenge", modifier = Modifier.weight(1f)) {
        display.value = true
    }

}


@Composable
private fun RowScope.Group(vm: TrackedActivityViewModel, groups: List<TrackerActivityGroup>?, activity: TrackedActivity?) {
    val display = remember { mutableStateOf(false) }

    if (activity != null && groups != null) DialogActivityGroupPicker(display = display, activity, groups) {
        vm.setGroup(it)
        display.value = false
    }

    IconTextButton(Icons.Default.Topic, stringResource(id = R.string.activity_screen_recent_group), modifier = Modifier.weight(1f)) {
        display.value = true
    }

}




@Composable
private fun RecentActivity(nav: NavController, state: TrackedActivityState?) {
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(20.dp)) {

        Column(
            Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
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

            if(state != null && state.activity.challenge.isSet()){
                GoalProgressBar(challenge = state.activity.challenge, actual = state.challengeMetric, type = state.activity.type )

                if(state.activity.isGoalSet()){
                    val aheadInDays = state.activity.getChallengeAheadDays(state.challengeMetric)

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = stringResource(R.string.ahead_of_challenge_deadline_note, aheadInDays),
                        style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center, fontSize = 12.sp)
                    )
                }
            }



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
                        text = it.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).uppercase(),
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

