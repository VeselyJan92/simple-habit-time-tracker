package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBundle
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.DashboardBody
import com.imfibit.activitytracker.core.extensions.toThemeColor
import com.imfibit.activitytracker.ui.components.EmptyState
import com.imfibit.activitytracker.ui.components.EmptyStateButton
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivity
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivityRecentOverview
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivity as TrackedActivityEntity
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.core.enums.MetricStatus
import com.imfibit.activitytracker.database.composed.MetricAggregation

@Composable
fun ScreenFocusBoard() {
    val viewModel: ScreenFocusBoardViewModel = hiltViewModel()
    val navigation = hiltViewModel<BackstackViewModel>()

    val data by viewModel.data.collectAsStateWithLifecycle()

    ScreenFocusBoardContent(
        data = data,
        trackedActivities = listOf(mockTrackedActivity), // TODO: connect to viewModel
        onBundleClick = { bundleId ->
            navigation.navigate(Destinations.ScreenFocusBundleDetail(bundleId))
        },
        onNoteClick = { bundleId, noteId ->
            navigation.navigate(Destinations.ScreenFocusBundleDetail(bundleId))
            navigation.navigate(Destinations.ScreemEditFocusBoardItem(bundleId, noteId))
        },
        onSwapStarredItems = viewModel::swapStarredItems,
        onSwapBundles = viewModel::swapBundles,
        onAddNote = {
            viewModel.addNote(it)
        },
        onCreateBundleClick = {
            navigation.navigate(Destinations.ScreenFocusBundleDetail(0L))
        },
        onCompleteToggle = viewModel::toggleNoteCompletion
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text(
                text = stringResource(id = R.string.focus_board_title),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = AppTheme.colors.onSurface // Very dark gray for strong contrast
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenFocusBoardContent(
    data: ScreenFocusBoardViewModel.Data?,
    trackedActivities: List<TrackedActivityRecentOverview> = emptyList(),
    onBundleClick: (Long) -> Unit,
    onNoteClick: (bundleId: Long, noteId: Long) -> Unit,
    onSwapStarredItems: (fromNoteId: Long, toNoteId: Long) -> Unit,
    onSwapBundles: (fromBundleId: Long, toBundleId: Long) -> Unit,
    onAddNote: (bundleId: Long) -> Unit,
    onCreateBundleClick: () -> Unit,
    onCompleteToggle: (FocusBoardItem) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromKey = from.key.toString()
        val toKey = to.key.toString()

        if (fromKey.startsWith("note_") && toKey.startsWith("note_")) {
            onSwapStarredItems(fromKey.removePrefix("note_").toLong(), toKey.removePrefix("note_").toLong())
        } else if (fromKey.startsWith("bundle_") && toKey.startsWith("bundle_")) {
            onSwapBundles(fromKey.removePrefix("bundle_").toLong(), toKey.removePrefix("bundle_").toLong())
        }
    }



    Surface(
        color = Color.Transparent, // Let the global DashboardScreen background bleed through
        modifier = Modifier.fillMaxSize()
    ) {
        DashboardBody {

            TopBar()

            if (data != null) {
                if (data.bundles.isEmpty() && data.staredItems.isEmpty() && trackedActivities.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Assignment,
                        title = stringResource(id = R.string.focus_board_empty_title),
                        description = stringResource(id = R.string.focus_board_empty_desc),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState,
                        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp, start = 12.dp, end = 12.dp)
                    ) {
                        // --- Global Pinned Items ---
                        if (data.staredItems.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.focus_board_pinned),
                                    style = MaterialTheme.typography.titleSmall, color = AppTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                            }

                            itemsIndexed(items = data.staredItems, key = { index, item -> "note_${item.item.id}" }) { index, item ->
                                ReorderableItem(
                                    state = reorderableState,
                                    key = "note_${item.item.id}"
                                ) { isDragging ->
                                    Column {
                                        FocusBoardItem(
                                            item = item,
                                            onClick = { onNoteClick(item.item.bundleId, item.item.id) },
                                            onCompleteToggle = { onCompleteToggle(item.item) },
                                            isDragging = isDragging,
                                            shape = getGroupedShape(index, data.staredItems.size),
                                            modifier = Modifier.longPressDraggableHandle()
                                        )
                                        if (index < data.staredItems.size - 1) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                        }
                                    }

                                }

                            }

                            item { Spacer(modifier = Modifier.height(24.dp)) }
                        }
                        
                        if (trackedActivities.isNotEmpty()) {
                            items(trackedActivities, key = { "activity_${it.activity.id}" }) { activity ->
                                TrackedActivity(
                                    item = activity,
                                    onNavigate = {},
                                    onActionButtonClick = {},
                                    onAddRecord = {}
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // --- Bundles List ---
                        if (data.bundles.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.focus_board_bundles),
                                    style = MaterialTheme.typography.titleSmall, color = AppTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                )
                            }
                        }

                        items(data.bundles, key = { "bundle_${it.id}" }) { bundle ->
                            ReorderableItem(reorderableState, "bundle_${bundle.id}") { isDragging ->
                                BundleCard(
                                    bundle = bundle,
                                    onClick = { onBundleClick(bundle.id) },
                                    onAddClick = { onAddNote(bundle.id) },
                                    isDragging = isDragging,
                                    modifier = Modifier
                                        .longPressDraggableHandle()
                                        .padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BundleCard(
    bundle: FocusBundle,
    onClick: () -> Unit,
    onAddClick: () -> Unit,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isDragging) AppTheme.colors.draggingBackground else AppTheme.colors.surfaceContainerLow.copy(alpha = 0.7f), // Frosted glass
        shadowElevation = 0.dp // Flat
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented Colored Ring Representation (Using PieChart icon as a placeholder)
            // A real version would use Canvas to draw segmented arcs
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = bundle.color.toThemeColor().takeIf { it.toArgb() != 0 } ?: AppTheme.colors.appAccent,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = bundle.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurface, // Dark solid text
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = CircleShape,
                color = Color.Transparent, // Matches background exactly with no outline
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAddClick)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.iconBackground.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.focus_board_add_inside_bundle),
                        tint = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewScreenFocusBoard() = AppTheme {


    ScreenFocusBoardContent(
        data = ScreenFocusBoardViewModel.Data(
            bundles = DevSeeder.getFocusBundles(),
            staredItems = DevSeeder.getPinnedNotes(),
        ),
        trackedActivities = listOf(mockTrackedActivity),
        onBundleClick = {},
        onNoteClick = { _, _ -> },
        onSwapStarredItems = { _, _ -> },
        onAddNote = { },
        onSwapBundles = { _, _ -> },
        onCreateBundleClick = {},
        onCompleteToggle = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenFocusBoardEmpty() = AppTheme {
    ScreenFocusBoardContent(
        data = ScreenFocusBoardViewModel.Data(
            bundles = emptyList(),
            staredItems = emptyList(),
        ),
        trackedActivities = emptyList(),
        onBundleClick = {},
        onNoteClick = { _, _ -> },
        onSwapStarredItems = { _, _ -> },
        onAddNote = { },
        onSwapBundles = { _, _ -> },
        onCreateBundleClick = {},
        onCompleteToggle = {}
    )
}
