package com.imfibit.activitytracker.ui.screens.activity_list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.navigation.navigate
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.DashboardBody
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.util.TestableContent
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.time.LocalDateTime

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenActivities(
    navController: NavHostController,
    vm: ActivitiesViewModel = hiltViewModel(),
) = TestableContent(testTag = TestTag.DASHBOARD_ACTIVITIES_CONTENT) {

    val recordVM = hiltViewModel<RecordViewModel>()

    DashboardBody {
        TopBar(
            onGoToSettings = {
                navController.navigate(Destinations.ScreenSettings)
            }
        )
        ScreenBody(
            vm,
            onNavigateToStatistics = {
                navController.navigate(Destinations.ScreenStatistics)
            },
            onGoToActivityGroup = {
                navController.navigate(Destinations.ScreenActivityGroupRoute(it))
            },
            onGoToActivity = {
                navController.navigate(Destinations.ScreenActivity(it.id))
            },
            onActionButtonClicked = {
                recordVM.activityTriggered(it)
            },
            onAddRecord = {
                when(it){
                    is TrackedActivityCompletion -> throw IllegalStateException("Completion not supported")
                    is TrackedActivityScore -> navController.navigate(
                        "dialog_edit_record/{record}",
                        bundleOf(
                            "record" to TrackedActivityScore(
                                activity_id = it.activity_id,
                                datetime_completed = LocalDateTime.now(),
                                score = 1
                            )
                        )
                    )
                    is TrackedActivityTime -> navController.navigate(
                        "dialog_edit_record/{record}",
                        bundleOf(
                            "record" to TrackedActivityTime(
                                activity_id = it.activity_id,
                                datetime_start = LocalDateTime.now(),
                                datetime_end = LocalDateTime.now()
                            )
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun TopBar(onGoToSettings: () -> Unit) {
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
            modifier = Modifier.clickable(
                onClick = onGoToSettings
            ),
            imageVector = Icons.Default.Settings,
            tint = Color.Black,
            contentDescription = null,
        )
    }

}

private fun Any?.type() = (this as String).split("_").first()

@Composable
private fun ScreenBody(
    vm: ActivitiesViewModel,
    onNavigateToStatistics: () -> Unit,
    onGoToActivityGroup: (Long) -> Unit,
    onGoToActivity: (TrackedActivity) -> Unit,
    onActionButtonClicked: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
) {
    val data by vm.data.collectAsStateWithLifecycle()

    Box {
        if (data.activities.isEmpty() && data.live.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 8.dp),
                    imageVector = Icons.Outlined.AssignmentTurnedIn,
                    contentDescription = "Focus item"
                )

                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = "Add tracked habit",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp
                )

                Text(text = "You can track: completion, time, score.")



                Spacer(modifier = Modifier.padding(top = 200.dp))

                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(bottom = 8.dp),
                    imageVector = Icons.Outlined.Swipe,
                    contentDescription = null
                )

                Text(text = stringResource(R.string.dashboard_explore))

            }
        }

        Activities(
            vm = vm,
            data = data,
            onNavigateToStatistics = onNavigateToStatistics,
            onGoToActivityGroup = onGoToActivityGroup,
            onGoToActivity = onGoToActivity,
            onActionButtonClick = onActionButtonClicked,
            onAddRecord = onAddRecord
        )

    }
}

@Composable
private fun Activities(
    vm: ActivitiesViewModel,
    data: ActivitiesViewModel.Data,
    onNavigateToStatistics: () -> Unit,
    onGoToActivityGroup: (Long) -> Unit,
    onGoToActivity: (TrackedActivity) -> Unit,
    onActionButtonClick: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
) {
    val lazyListState = rememberLazyGridState()
    val reorderableLazyListState = rememberReorderableLazyGridState(lazyListState) { from, to ->
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
        }
    }

    LazyVerticalGrid(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        item(
            key = "head",
            span = { GridItemSpan(2) }
        ) {
            Today(
                today = data.today,
                onNavigateToStatistics = onNavigateToStatistics
            )
        }

        items(
            items = data.activities,
            key = { "activity_${it.activity.id}" },
            span = { GridItemSpan(2) }
        ) { item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = "activity_${item.activity.id}"
            ) { isDragging ->
                TrackedActivity(
                    modifier = Modifier.longPressDraggableHandle(),
                    item = item,
                    onNavigate = onGoToActivity,
                    isDragging = isDragging,
                    onActionButtonClick = onActionButtonClick,
                    onAddRecord = onAddRecord
                )
            }
        }

        items(
            items = data.groups,
            key = { "group_${it.id}" },
            span = { GridItemSpan(1) }
        ) { item ->
            ReorderableItem(
                enabled = reorderableLazyListState.isAnyItemDragging,
                state = reorderableLazyListState,
                key = "group_${item.id}"
            ) { isDragging ->
                ActivityGroup(
                    modifier = Modifier.longPressDraggableHandle(),
                    item = item,
                    color = Color.White,
                    onGoToActivityGroup = onGoToActivityGroup
                )
            }
        }
    }
}

@Composable
private fun Today(
    today: List<ActivityWithMetric>,
    onNavigateToStatistics: () -> Unit,
) {
    Surface(
        shadowElevation = 2.dp,
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
                    onClick = onNavigateToStatistics
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_title_statistics),
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                        )
                    )
                }

            }

            if (today.isEmpty()) {
                Box(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_records),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                today.forEachIndexed { index, item ->
                    val xx =
                        item.activity.goal.range == TimeRange.DAILY && item.metric < item.activity.goal.value;

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

                    if (index != today.size - 1) {
                        HorizontalDivider(Modifier.padding(top = 4.dp, bottom = 4.dp))
                    }
                }

            }


        }

    }
}

@Composable
private fun ActivityGroup(
    modifier: Modifier,
    item: TrackerActivityGroup,
    color: Color = Color.White,
    onGoToActivityGroup: (Long) -> Unit,
) {
    Surface(
        shadowElevation = 2.dp,
        modifier = modifier
            .clickable(
                onClick = {
                    onGoToActivityGroup(item.id)
                }
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
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
