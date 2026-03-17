package com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.DashboardBody
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.core.enums.MetricStatus
import com.imfibit.activitytracker.ui.components.EmptyState
import com.imfibit.activitytracker.ui.components.EmptyStateHint
import com.imfibit.activitytracker.ui.components.util.TestableContent
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivityRecentOverview
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenTrackedActivities(
    vm: ScreenTrackedActivitiesViewModel = hiltViewModel(),
) = TestableContent(testTag = TestTag.DASHBOARD_ACTIVITIES_CONTENT) {

    val recordVM = hiltViewModel<RecordViewModel>()
    val navigation = hiltViewModel<BackstackViewModel>()
    val data by vm.data.collectAsStateWithLifecycle()

    ScreenActivitiesContent(
        data = data,
        onGoToSettings = { navigation.navigate(Destinations.ScreenSettings) },
        onNavigateToStatistics = { navigation.navigate(Destinations.ScreenStatistics) },
        onGoToActivityGroup = { navigation.navigate(Destinations.ScreenActivityGroupRoute(it)) },
        onGoToActivity = { navigation.navigate(Destinations.ScreenActivity(it.id)) },
        onActionButtonClicked = { recordVM.activityTriggered(it) },
        onAddRecord = {
            when(it) {
                is TrackedActivityCompletion -> throw IllegalStateException("Completion not supported")
                is TrackedActivityScore -> navigation.navigate(
                    Destinations.DialogEditRecord(
                        TrackedActivityScore(
                            activity_id = it.activity_id,
                            datetime_completed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                            score = 1
                        )
                    )
                )
                is TrackedActivityTime -> navigation.navigate(
                    Destinations.DialogEditRecord(
                        TrackedActivityTime(
                            activity_id = it.activity_id,
                            datetime_start = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                            datetime_end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    )
                )
            }
        },
        onMoveActivity = vm::onMoveActivity,
        onMoveGroup = vm::onMoveGroup
    )
}

@Composable
fun ScreenActivitiesContent(
    data: ScreenTrackedActivitiesViewModel.Data?,
    onGoToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onGoToActivityGroup: (Long) -> Unit,
    onGoToActivity: (TrackedActivity) -> Unit,
    onActionButtonClicked: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
    onMoveActivity: (Int, Int) -> Unit,
    onMoveGroup: (Int, Int) -> Unit
) {
    Surface(
        color = Color.Transparent, // Let the global DashboardScreen background bleed through
        modifier = Modifier.fillMaxSize()
    ) {
        DashboardBody {
            TopBar(onGoToSettings = onGoToSettings)
            if (data != null) {
                ScreenBody(
                    data = data,
                    onNavigateToStatistics = onNavigateToStatistics,
                    onGoToActivityGroup = onGoToActivityGroup,
                    onGoToActivity = onGoToActivity,
                    onActionButtonClicked = onActionButtonClicked,
                    onAddRecord = onAddRecord,
                    onMoveActivity = onMoveActivity,
                    onMoveGroup = onMoveGroup
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onGoToSettings: () -> Unit) {
    TopAppBar(
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text(
                text = stringResource(id = R.string.screen_activities_tracked_habits),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = AppTheme.colors.onSurface
            )
        },
        actions = {
            Surface(
                shape = RoundedCornerShape(50),
                color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.7f),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
            ) {
                IconButton(onClick = onGoToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        tint = AppTheme.colors.onSurfaceVariant,
                        contentDescription = stringResource(id = R.string.screen_settings_title),
                    )
                }
            }
        }
    )
}

private fun Any?.type() = (this as String).split("_").first()
private fun Any?.id() = (this as String).split("_").getOrNull(1)?.toLongOrNull()

@Composable
private fun ScreenBody(
    data: ScreenTrackedActivitiesViewModel.Data,
    onNavigateToStatistics: () -> Unit,
    onGoToActivityGroup: (Long) -> Unit,
    onGoToActivity: (TrackedActivity) -> Unit,
    onActionButtonClicked: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
    onMoveActivity: (Int, Int) -> Unit,
    onMoveGroup: (Int, Int) -> Unit
) {
    Box {
        if (data.activities.isEmpty() && data.live.isEmpty() && data.groups.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.AssignmentTurnedIn,
                title = stringResource(id = R.string.screen_activities_tracked_habits),
                description = stringResource(id = R.string.screen_onboarding_page_habits_text),
                footer = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(bottom = 8.dp),
                            imageVector = Icons.Outlined.Swipe,
                            tint = AppTheme.colors.outlineVariant,
                            contentDescription = null
                        )

                        Text(
                            text = stringResource(id = R.string.screen_activities_swipe_explore),
                            color = AppTheme.colors.outline
                        )
                    }
                }
            )
        } else {
            Activities(
                data = data,
                onNavigateToStatistics = onNavigateToStatistics,
                onGoToActivityGroup = onGoToActivityGroup,
                onGoToActivity = onGoToActivity,
                onActionButtonClick = onActionButtonClicked,
                onAddRecord = onAddRecord,
                onMoveActivity = onMoveActivity,
                onMoveGroup = onMoveGroup
            )
        }
    }
}

@Composable
private fun Activities(
    data: ScreenTrackedActivitiesViewModel.Data,
    onNavigateToStatistics: () -> Unit,
    onGoToActivityGroup: (Long) -> Unit,
    onGoToActivity: (TrackedActivity) -> Unit,
    onActionButtonClick: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
    onMoveActivity: (Int, Int) -> Unit,
    onMoveGroup: (Int, Int) -> Unit
) {
    val lazyListState = rememberLazyGridState()
    
    // Safer Reorder Logic based on actual item IDs instead of index math
    val reorderableLazyListState = rememberReorderableLazyGridState(lazyListState) { from, to ->
        when {
            from.key.type() == "activity" && to.key.type() == "activity" -> {
                val fromId = from.key.id() ?: return@rememberReorderableLazyGridState
                val toId = to.key.id() ?: return@rememberReorderableLazyGridState
                val fromIndex = data.activities.indexOfFirst { it.activity.id == fromId }
                val toIndex = data.activities.indexOfFirst { it.activity.id == toId }
                if (fromIndex != -1 && toIndex != -1) onMoveActivity(fromIndex, toIndex)
            }
            from.key.type() == "group" && to.key.type() == "group" -> {
                val fromId = from.key.id() ?: return@rememberReorderableLazyGridState
                val toId = to.key.id() ?: return@rememberReorderableLazyGridState
                val fromIndex = data.groups.indexOfFirst { it.id == fromId }
                val toIndex = data.groups.indexOfFirst { it.id == toId }
                if (fromIndex != -1 && toIndex != -1) onMoveGroup(fromIndex, toIndex)
            }
        }
    }

    LazyVerticalGrid(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Tighter spacing
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // --- 1. Today Overview (Full Width) ---
        item(
            key = "head",
            span = { GridItemSpan(2) }
        ) {
            Today(
                today = data.today,
                onNavigateToStatistics = onNavigateToStatistics
            )
        }

        // --- 2. Tracked Activities (Full Width) ---
        items(
            items = data.activities,
            key = { "activity_${it.activity.id}" },
            span = { GridItemSpan(2) }
        ) { item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = "activity_${item.activity.id}"
            ) { isDragging ->
                com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivity(
                    modifier = Modifier.longPressDraggableHandle(),
                    item = item,
                    onNavigate = onGoToActivity,
                    isDragging = isDragging,
                    onActionButtonClick = onActionButtonClick,
                    onAddRecord = onAddRecord
                )
            }
        }

        // --- 3. Folders / Groups Section (Grid of compact tiles) ---
        if (data.groups.isNotEmpty()) {
            item(
                key = "groups_header",
                span = { GridItemSpan(2) }
            ) {
                Text(
                    text = stringResource(id = R.string.screen_activities_folders),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AppTheme.colors.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 12.dp, start = 4.dp, bottom = 4.dp)
                )
            }

            items(
                items = data.groups,
                key = { "group_${it.id}" },
                span = { GridItemSpan(1) } // These use the 2-column grid effectively
            ) { item ->
                ReorderableItem(
                    enabled = reorderableLazyListState.isAnyItemDragging,
                    state = reorderableLazyListState,
                    key = "group_${item.id}"
                ) { isDragging ->
                    ActivityGroup(
                        modifier = Modifier.longPressDraggableHandle(),
                        item = item,
                        isDragging = isDragging,
                        onGoToActivityGroup = onGoToActivityGroup
                    )
                }
            }
        }

        // Spacer to prevent FAB overlapping content
        item(
            key = "spacer",
            span = { GridItemSpan(2) }
        ) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun Today(
    today: List<ActivityWithMetric>,
    onNavigateToStatistics: () -> Unit,
) {
    Surface(
        shadowElevation = 0.dp, // Flat transparent look
        shape = RoundedCornerShape(12.dp), // Matched smaller corner size
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.7f) // Translucent glass effect
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.screen_activities_today),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppTheme.colors.onSurface // Deep bold contrast
                    )
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.6f), // Subtle contrast on frosted bg
                    modifier = Modifier.clickable(onClick = onNavigateToStatistics)
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_activities_stats),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AppTheme.colors.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            if (today.isEmpty()) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_records),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppTheme.colors.outline
                    )
                }
            } else {
                today.forEachIndexed { index, item ->
                    val isGoalMissed = item.activity.goal.range == TimeRange.DAILY && item.metric < item.activity.goal.value;

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.activity.name, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.onSurface
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        val label = item.activity.type.getLabel(item.metric).value()
                        val color = if (isGoalMissed) AppTheme.colors.surfaceVariant else AppTheme.colors.primary
                        val textColor = if (isGoalMissed) AppTheme.colors.onSurfaceVariant else AppTheme.colors.onPrimary

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = color,
                            modifier = Modifier.height(24.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }

                    if (index != today.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp), 
                            color = AppTheme.colors.surfaceVariant.copy(alpha = 0.5f)
                        )
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
    isDragging: Boolean,
    onGoToActivityGroup: (Long) -> Unit,
) {
    Surface(
        shadowElevation = 0.dp,
        modifier = modifier
            .clickable(onClick = { onGoToActivityGroup(item.id) })
            .fillMaxWidth()
            .height(56.dp), // Tighter compact folder pill
        shape = RoundedCornerShape(16.dp),
        color = if (isDragging) AppTheme.colors.surfaceVariant.copy(alpha = 0.7f) else AppTheme.colors.surfaceContainerLow.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier.padding(end = 12.dp).size(20.dp),
                imageVector = Icons.Outlined.Folder,
                tint = AppTheme.colors.primary, // Deep purple folder icon
                contentDescription = null
            )

            Text(
                text = item.name,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppTheme.colors.onSurface // High contrast text
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Outlined.Swipe,
                contentDescription = stringResource(id = R.string.screen_group_reorder),
                tint = AppTheme.colors.outline.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenActivitiesPreview() = AppTheme {
    val mockActivity = TrackedActivity(
        id = 1,
        name = "Read Book",
        position = 0,
        type = TrackedActivity.Type.TIME,
        inSessionSince = null,
        goal = TrackedActivityGoal(3600L, TimeRange.DAILY),
        challenge = TrackedActivityChallenge.empty
    )

    val mockActivityOverview = TrackedActivityRecentOverview(
        activity = mockActivity,
        challengeMetric = 0L,
        past = List(5) { MetricWidgetData(value = { "0" }, status = MetricStatus.DEFAULT) },
        actionButton = TrackedActivityRecentOverview.ActionButton.DEFAULT,
        today = MetricAggregation(
            from = LocalDate(2023, 10, 24),
            to = LocalDate(2023, 10, 24),
            metric = 0L
        )
    )

    val mockGroup1 = TrackerActivityGroup(id = 1, name = "Fitness", position = 0)
    val mockGroup2 = TrackerActivityGroup(id = 2, name = "Work", position = 1)
    val mockActivityWithMetric = ActivityWithMetric(activity = mockActivity, metric = 1800L)

    val mockData = ScreenTrackedActivitiesViewModel.Data(
        activities = listOf(mockActivityOverview),
        live = emptyList(),
        today = listOf(mockActivityWithMetric),
        groups = listOf(mockGroup1, mockGroup2)
    )

    ScreenActivitiesContent(
        data = mockData,
        onGoToSettings = {},
        onNavigateToStatistics = {},
        onGoToActivityGroup = {},
        onGoToActivity = {},
        onActionButtonClicked = {},
        onAddRecord = {},
        onMoveActivity = { _, _ -> },
        onMoveGroup = { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
fun ScreenActivitiesEmptyPreview() = AppTheme {
    val mockData = ScreenTrackedActivitiesViewModel.Data(
        activities = emptyList(),
        live = emptyList(),
        today = emptyList(),
        groups = emptyList()
    )

    ScreenActivitiesContent(
        data = mockData,
        onGoToSettings = {},
        onNavigateToStatistics = {},
        onGoToActivityGroup = {},
        onGoToActivity = {},
        onActionButtonClicked = {},
        onAddRecord = {},
        onMoveActivity = { _, _ -> },
        onMoveGroup = { _, _ -> }
    )
}
