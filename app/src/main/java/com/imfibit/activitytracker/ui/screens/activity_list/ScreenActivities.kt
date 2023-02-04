package com.imfibit.activitytracker.ui.screens.activity_list

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITY
import com.imfibit.activitytracker.ui.SCREEN_SETTINGS
import com.imfibit.activitytracker.ui.SCREEN_STATISTICS
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.burnoutcrew.reorderable.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenActivities(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        runBlocking {
            context.dataStore.edit { settings -> settings[PreferencesKeys.NOTIFICATION_ALLOWED] = true }
        }
    }

    coroutineScope.launch(Dispatchers.IO) {
        val promptNotification = context.dataStore.data.first()[PreferencesKeys.NOTIFICATION_ALLOWED] ?: false

        delay(2000)

        if (promptNotification){
            startForResult.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    val vm = hiltViewModel<ActivitiesViewModel>()
    val display = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    val newGroup =
        TrackerActivityGroup(0, stringResource(id = R.string.screen_activities_new_group), 0)

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
                    goal = TrackedActivityGoal(0L, TimeRange.WEEKLY),
                    challenge = TrackedActivityChallenge.empty
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
        scaffoldState = scaffoldState,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, bottom = 8.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Simple Habit Tracker",
                    fontWeight = FontWeight.Black, fontSize = 25.sp
                )

                Icon(
                    modifier = Modifier.clickable {
                        navController.navigate(SCREEN_SETTINGS)
                    },
                    imageVector = Icons.Default.Settings,
                    tint = Color.Black,
                    contentDescription = null,
                )
            }


            /*TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)){

            }*/
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { display.value = true }) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = {
            ScreenBody(navController, vm)
        },
        backgroundColor = Colors.AppBackground
    )
}

private fun Any?.type() = (this as String).split("_").first()

@Composable
private fun ScreenBody(
    nav: NavHostController,
    vm: ActivitiesViewModel
) {
    val data by vm.data.collectAsState(initial = ActivitiesViewModel.Data())

    Log.e("Screen", "invalidate")

    val state = rememberReorderableLazyGridState(
        onDragEnd = {
                from, to -> vm.onDragEnd()
        },
        canDragOver = {
                draggedOver, dragging -> draggedOver.key.type() == dragging.key.type()
        },
        onMove = { from, to ->
            val itemsBefore = data.activities.size + data.live.size + 1

            when {
                from.key.type() == "activity" && to.key.type() == "activity" -> vm.onMoveActivity(
                    from.index - (data.live.size + 1),
                    to.index - (data.live.size + 1)
                )
                from.key.type() == "group" && to.key.type() == "group" -> vm.onMoveGroup(
                    from.index - itemsBefore,
                    to.index - itemsBefore
                )
                else -> throw IllegalArgumentException()
            }
        }
    )

    LazyVerticalGrid(
        state = state.gridState,
        modifier = Modifier
            .reorderable(
                state = state,
            ),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {

        item("head", span = { GridItemSpan(2) }) {
            val context = LocalContext.current
            val pref by context.dataStore.data.collectAsState(initial = null)

            if (pref?.get(PreferencesKeys.ERASE_OBOARDING_SHOW) == true) {
                ClearAll(vm)
            }

            Today(nav, data.today)
        }


        items(data.live, {"other_${it.activity.id}"},span = { GridItemSpan(2) }) { item->
            ReorderableItem(reorderableState = state , key = "other_${item.activity.id}") {
                TrackedActivity(
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(SCREEN_ACTIVITY(it.id.toString())) },
                    isDragging = false
                )
            }

        }

        items(data.activities, { "activity_${it.activity.id}" }, span = { GridItemSpan(2) }) { item ->
            ReorderableItem(
                state,
                key = "activity_${item.activity.id}",
                modifier = Modifier.detectReorderAfterLongPress(state)
            ) { isDragging ->
                TrackedActivity(
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(SCREEN_ACTIVITY(it.id.toString())) },
                    isDragging = isDragging
                )
            }
        }


        items(data.groups, { "group_${it.id}" }, span = { GridItemSpan(1) }) { item ->
            ReorderableItem(
                state,
                "group_${item.id}",
                modifier = Modifier.detectReorderAfterLongPress(state)
            ) { isDragging ->
                Category(nav, item, if (isDragging) Color.LightGray else Color.White )
            }
        }
    }
}


@Composable
private fun ClearAll(vm: ActivitiesViewModel) {

    val context = LocalContext.current

    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            ) {
                Text(text = "Erase")
            }

        }
    }
}

@Composable
private fun Today(nav: NavHostController, today: List<ActivityWithMetric>) {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        color = Colors.SuperLight
    ) {

        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.screen_activities_today),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp
                    )
                )


                TextButton(
                    onClick = { nav.navigate(SCREEN_STATISTICS) }
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_title_statistics),
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                        )
                    )
                }

            }

            if (today.isEmpty()){
                Box(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.Center){
                    Text(text = stringResource(id = R.string.no_records), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }else{
                today.forEachIndexed { index, item ->
                    val xx = item.activity.goal.range == TimeRange.DAILY && item.metric < item.activity.goal.value;

                    Row(Modifier.padding(start = 8.dp, end = 8.dp)) {

                        Text(text = item.activity.name, fontSize = 16.sp)

                        Spacer(modifier = Modifier.weight(1f))

                        val label = item.activity.type.getLabel(item.metric).value()

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
}

@Composable
private fun Category(nav: NavHostController, item: TrackerActivityGroup, color: Color = Color.White) {
    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { nav.navigate("screen_activity_group/${item.id}") }
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = color
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

}
