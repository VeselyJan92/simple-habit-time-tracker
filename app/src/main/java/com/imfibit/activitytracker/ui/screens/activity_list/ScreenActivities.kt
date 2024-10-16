package com.imfibit.activitytracker.ui.screens.activity_list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.MainBody
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.util.TestableContent
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenActivities(
    navController: NavHostController,
    vm:ActivitiesViewModel  = hiltViewModel()
) = TestableContent(testTag = TestTag.DASHBOARD_ACTIVITIES_CONTENT) {
    MainBody {
        TopBar(nav = navController)
        ScreenBody(navController, vm)
    }
}

@Composable
private fun TopBar(nav: NavHostController){
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
                nav.navigate(Destinations.ScreenSettings)
            },
            imageVector = Icons.Default.Settings,
            tint = Color.Black,
            contentDescription = null,
        )
    }

}

private fun Any?.type() = (this as String).split("_").first()

@Composable
private fun ScreenBody(
    nav: NavHostController,
    vm: ActivitiesViewModel
) {
    val data by vm.data.collectAsStateWithLifecycle()

    Box {
        if (data.activities.isEmpty() && data.live.isEmpty()){
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

        Activities(nav, vm, data)

    }
}

@Composable
private fun Activities(
    nav: NavHostController,
    vm: ActivitiesViewModel,
    data: ActivitiesViewModel.Data
) {
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
            Today(nav, data.today)
        }


        items(data.live, {"other_${it.activity.id}"},span = { GridItemSpan(2) }) { item->
            ReorderableItem(reorderableState = state , key = "other_${item.activity.id}") {
                TrackedActivity(
                    nav = nav,
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(Destinations.ScreenActivity(it.id)) },
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
                    nav = nav,
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(Destinations.ScreenActivity(it.id)) },
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
                    onClick = { nav.navigate(Destinations.ScreenStatistics) }
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
            ) { nav.navigate(Destinations.ScreenActivityGroupRoute(item.id)) }
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
