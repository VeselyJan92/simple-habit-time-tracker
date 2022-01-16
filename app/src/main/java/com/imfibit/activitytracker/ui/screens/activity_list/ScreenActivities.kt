package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITY
import com.imfibit.activitytracker.ui.SCREEN_SETTINGS
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.burnoutcrew.reorderable.*

@Composable
fun ScreenActivities(
    navController: NavHostController,
) {

    val vm = hiltViewModel<ActivitiesViewModel>()
    val display = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    val data by vm.data.collectAsState(initial = ActivitiesViewModel.Data())

    val newGroup = TrackerActivityGroup(0, stringResource(id = R.string.screen_activities_new_group), 0)

    DialogAddActivity(display = display,
        onAddFolder = {
            scope.launch {
                vm.addGroup(newGroup)
                display.value = false
            }
        },
        onAdd = {
            scope.launch {

                val activity = TrackedActivity(
                    id = 0L,
                    name = name,
                    position = 0,
                    type = it,
                    inSessionSince = null,
                    goal = TrackedActivityGoal(0L, TimeRange.WEEKLY)
                )

                val id = withContext(Dispatchers.IO) {
                    vm.addActivity(activity)
                }

                withContext(Dispatchers.Main) {
                    display.value = false
                    navController.navigate("screen_activity/$id")
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)){
                Icon(
                    modifier = Modifier.clickable {
                        navController.navigate(SCREEN_SETTINGS)
                    },
                    imageVector = Icons.Default.Settings,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { display.value = true }) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = {
            ScreenBody(navController, vm, data.activities, data.today, data.groups, data.live)
        },
        bottomBar = {
          Column {
                LiveActivitiesList(vm, data.live)
                AppBottomNavigation(navController)
            }
        },
        backgroundColor = Colors.AppBackground
    )
}

@Composable
private fun ScreenBody(
    nav: NavHostController,
    vm: ActivitiesViewModel,
    activities: List<TrackedActivityWithMetric>,
    today: List<ActivityWithMetric>,
    groups: List<TrackerActivityGroup>,
    live: List<TrackedActivity>,
) {

    val state: ReorderableState = rememberReorderState()

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .padding(8.dp)
            .reorderable(
                state = state,
                onDragEnd = { from, to -> vm.moveActivity() },
                onMove = { from, to ->
                    vm.dragActivity(from - 1, to - 1)
                }
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {

            val context = LocalContext.current

            val pref by context.dataStore.data.collectAsState(initial = null)

            if (pref?.get(PreferencesKeys.ERASE_OBOARDING_SHOW) != false){
                ClearAll(vm)
            }


            if(today.isNotEmpty())
                Today(today)
        }

        itemsIndexed(activities) { index, item ->
            TrackedActivity(
                item = item,
                modifier = Modifier
                    .draggedItem(state.offsetByIndex(index + 1))
                    .detectReorderAfterLongPress(state),
                onNavigate = {nav.navigate(SCREEN_ACTIVITY(it.id.toString()))}
            )
        }

        item {
            Categories(vm, groups, nav)
        }

        item {
            val bottom = 50.dp
            val add = 70.dp
            val live = (live.size * 56).dp

            Box(modifier = Modifier
                .height(bottom + add + live)
                .fillMaxWidth()
            )
        }



    }
}


@Composable
private fun ClearAll(vm: ActivitiesViewModel) {

    val context = LocalContext.current

    Surface(
        modifier = Modifier.padding(bottom = 8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier.clickable {
                    vm.hideClearCard(context)
                },
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = "Clear all onboarding data",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Box(
                modifier = Modifier
                    .size(70.dp, 30.dp)
                    .padding(end = 8.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))
                    .clickable(onClick = {
                        vm.clearOnboardingData(context)
                    }),
                contentAlignment = Alignment.Center
            ){
                Text(text = "Erase")
            }

        }
    }
}

@Composable
private fun Today(today: List<ActivityWithMetric>){
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {

        Column(
            Modifier.padding(8.dp)
        ) {

            Text(
                text = stringResource(id = R.string.screen_activities_today),
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp
                )
            )

            today.forEachIndexed{ index, item ->

                val xx = item.activity.goal.range == TimeRange.DAILY && item.metric < item.activity.goal.value ;

                Row(Modifier.padding(start = 8.dp, end = 8.dp)) {


                    Text(text = item.activity.name, fontSize = 16.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    val label = item.activity.type.getComposeString(item.metric).invoke()

                    val color = if (xx) Colors.NotCompleted else Colors.AppAccent

                    BaseMetricBlock(
                        metric = label,
                        color = color, metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                if (index != today.size - 1)
                    Divider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Categories(
    vm: ActivitiesViewModel,
    groups: List<TrackerActivityGroup>,
    nav: NavHostController
) {

    Column {
        for (row in groups.chunked(2)) {
            Row {
                for ((index, item) in row.withIndex()) {
                    Surface(
                        elevation = 2.dp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .clickable {
                                nav.navigate("screen_activity_group/${item.id}")
                            },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(30.dp),
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Icon(
                                modifier = Modifier.padding(end = 8.dp),
                                imageVector = Icons.Outlined.Topic,
                                contentDescription = null
                            )

                            Text(
                                text = item.name,
                                style = TextStyle(
                                    fontWeight = FontWeight.W500,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    if(index == 0)
                        Spacer(modifier = Modifier.width(8.dp))
                }

                if (row.size == 1){
                    Spacer(modifier = Modifier.weight(1f))
                }

            }
        }
    }

}