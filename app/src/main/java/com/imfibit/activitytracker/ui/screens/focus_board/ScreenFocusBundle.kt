package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.core.extensions.toThemeColor
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.embedable.Markdown
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.EmptyState
import com.imfibit.activitytracker.ui.components.darker
import com.imfibit.activitytracker.ui.components.editor.RichContentEditor
import com.imfibit.activitytracker.ui.components.editor.rememberRichContentEditorState
import com.imfibit.activitytracker.ui.components.harmonizeWithTheme
import com.imfibit.activitytracker.ui.components.topBar.SimpleBackTopBar
import com.imfibit.activitytracker.ui.screens.focus_board.components.BottomSheetBundleSettings
import com.imfibit.activitytracker.ui.screens.focus_board.components.FocusItemTag
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
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


val mockTrackedActivity = TrackedActivityRecentOverview(
    activity = TrackedActivityEntity(
        id = 1L,
        name = "Reading",
        type = TrackedActivityEntity.Type.TIME,
        goal = TrackedActivityGoal(3600L, TimeRange.DAILY),
        challenge = TrackedActivityChallenge.empty,
        inSessionSince = null
    ),
    challengeMetric = 0L,
    past = listOf(
        MetricWidgetData({ "01:00" }, status = MetricStatus.COMPLETED),
        MetricWidgetData({ "01:00" }, status = MetricStatus.DEFAULT),
        MetricWidgetData({ "01:00" }, status = MetricStatus.DEFAULT),
        MetricWidgetData({ "01:00" }, status = MetricStatus.COMPLETED),
        MetricWidgetData({ "01:00" }, status = MetricStatus.COMPLETED)
    ),
    actionButton = TrackedActivityRecentOverview.ActionButton.DEFAULT,
    today = MetricAggregation(
        from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        to = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        metric = 3600L
    )
)

@Preview(showBackground = true)
@Composable
private fun ScreenFocusBundlePreview() = AppTheme {
    // Create mock data
    val mockBundle = FocusBundle(id = 1L, title = "My Focus Board")
    val mockTags = listOf(
        FocusBoardItemTag(
            id = 1,
            name = "Work",
            color = Colors.chooseableColors.first().toArgb(),
            isChecked = false,
            isTaskTag = true
        ),
        FocusBoardItemTag(
            id = 2,
            name = "Personal",
            color = Colors.chooseableColors[4].toArgb(),
            isChecked = false,
            isTaskTag = false
        ),
        FocusBoardItemTag(
            id = 3,
            name = "Idea",
            color = Colors.chooseableColors[7].toArgb(),
            isChecked = false,
            isTaskTag = false
        )
    )
    val mockItems = listOf(
        FocusBoardItemWithTags(
            item = FocusBoardItem(
                id = 1,
                bundleId = 1L,
                bundlePosition = 1,
                title = "Short Task",
                content = Markdown(""),
                isPinned = true
            ),
            tags = listOf(mockTags[0])
        ),
        FocusBoardItemWithTags(
            item = FocusBoardItem(
                id = 2,
                bundleId = 1L,
                bundlePosition = 2,
                title = "Note with long content",
                content = Markdown("This is a longer note content to see how it looks in the list. It should be rendered correctly using the RichContentEditor."),
                isPinned = false
            ),
            tags = listOf(mockTags[1])
        ),
        FocusBoardItemWithTags(
            item = FocusBoardItem(
                id = 3,
                bundleId = 1L,
                bundlePosition = 3,
                title = "Checked Task",
                content = Markdown("Already finished this one"),
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            ),
            tags = listOf(mockTags[0])
        ),
        FocusBoardItemWithTags(
            item = FocusBoardItem(
                id = 4,
                bundleId = 1L,
                bundlePosition = 4,
                title = "Another active task",
                content = Markdown("Needs attention"),
                isPinned = false
            ),
            tags = listOf(mockTags[0], mockTags[2])
        )
    )



    ScreenFocusBundleContent(
        bundle = mockBundle,
        focusItems = mockItems,
        tags = mockTags,
        trackedActivities = listOf(mockTrackedActivity),
        onBack = {},
        onTagToggle = {},
        onFocusItemClick = {},
        onFocusItemAdd = {},
        onSwapFocusItems = { _, _ -> },
        onBundleSave = { _, _ -> },
        onBundleDelete = {},
        onCompleteToggle = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun ScreenFocusBundleEmptyPreview() = AppTheme {
    // Create mock data
    val mockBundle = FocusBundle(id = 1L, title = "My Focus Board")

    ScreenFocusBundleContent(
        bundle = mockBundle,
        focusItems = emptyList(),
        tags = emptyList(),
        trackedActivities = emptyList(),
        onBack = {},
        onTagToggle = {},
        onFocusItemClick = {},
        onFocusItemAdd = {},
        onSwapFocusItems = { _, _ -> },
        onBundleSave = { _, _ -> },
        onBundleDelete = {},
        onCompleteToggle = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFocusBundle(bundleId: Long) {
    val viewModel = hiltViewModel<ScreenFocusBundleViewModel, ScreenFocusBundleViewModel.Factory> { factory ->
        factory.create(bundleId)
    }

    val navigation = hiltViewModel<BackstackViewModel>()

    val data by viewModel.data.collectAsStateWithLifecycle()

    data?.let {
        ScreenFocusBundleContent(
            bundle = it.bundle,
            focusItems = it.focusItems,
            tags = it.tags,
            trackedActivities = listOf(mockTrackedActivity), // TODO: populate from viewmodel later
            onBack = { navigation.popBackStack() },
            onTagToggle = viewModel::onTagToggle,
            onFocusItemClick = { item ->
                navigation.navigate(Destinations.ScreemEditFocusBoardItem(bundleId, item.item.id))
            },
            onFocusItemAdd = {
                viewModel.addFocusItem()
            },
            onSwapFocusItems = viewModel::swapFocusItems,
            onBundleSave = viewModel::onBundleSave,
            onBundleDelete = { bundle ->
                viewModel.onBundleDelete(bundle)
                navigation.popBackStack()
            },
            onCompleteToggle = viewModel::onCompleteToggle
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenFocusBundleContent(
    bundle: FocusBundle,
    focusItems: List<FocusBoardItemWithTags>,
    tags: List<FocusBoardItemTag>,
    trackedActivities: List<TrackedActivityRecentOverview> = emptyList(),
    onBack: () -> Unit,
    onTagToggle: (FocusBoardItemTag) -> Unit,
    onFocusItemClick: (FocusBoardItemWithTags) -> Unit,
    onFocusItemAdd: () -> Unit,
    onSwapFocusItems: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onBundleSave: (FocusBundle, List<FocusBoardItemTag>) -> Unit,
    onBundleDelete: (FocusBundle) -> Unit,
    onCompleteToggle: (FocusBoardItemWithTags) -> Unit,
) {
    Scaffold(
        containerColor = AppTheme.colors.lightBackground,
        topBar = {
            TopBar(
                bundle = bundle,
                tagsState = tags,
                onBundleSave = onBundleSave,
                onBundleDelete = onBundleDelete,
                onBack = onBack
            )
        },
        floatingActionButton = {
            BottomFloatingBar(onAddClick = onFocusItemAdd)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            FocusBoardItems(
                focusItems = focusItems,
                trackedActivities = trackedActivities,
                tags = tags,
                onTagToggle = onTagToggle,
                swapFocusItems = onSwapFocusItems,
                onFocusItemClick = onFocusItemClick,
                onCompleteToggle = onCompleteToggle
            )
        }
    }
}

@Composable
private fun BottomFloatingBar(
    onAddClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onAddClick,
        containerColor = AppTheme.colors.primary,
        contentColor = AppTheme.colors.onPrimary,
        modifier = Modifier.size(48.dp),
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.focus_board_add_inside_bundle))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    bundle: FocusBundle,
    tagsState: List<FocusBoardItemTag>,
    onBundleSave: (FocusBundle, List<FocusBoardItemTag>) -> Unit,
    onBundleDelete: (FocusBundle) -> Unit,
    onBack: () -> Unit,
) {
    var editSettings by remember { mutableStateOf(false) }
    if (editSettings) {
        BottomSheetBundleSettings(
            onDismissRequest = { editSettings = false },
            bundle = bundle,
            onBundleSave = onBundleSave,
            onBundleDelete = onBundleDelete,
            tags = tagsState,
        )
    }

    SimpleBackTopBar(
        title = bundle.title,
        onBack = onBack,
        endIcon = {
            IconButton(onClick = { editSettings = true }) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.screen_settings_title))
            }
        }
    )
}

@Composable
private fun HeaderWithTags(
    onToggle: (FocusBoardItemTag) -> Unit,
    tags: List<FocusBoardItemTag>,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {

        items(tags, { item -> item.id }) { item ->
            FocusItemTag(
                name = item.name,
                isSelected = item.isChecked,
                onClick = { onToggle(item) },
                color = item.color.toThemeColor(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FocusBoardItems(
    onFocusItemClick: (FocusBoardItemWithTags) -> Unit,
    focusItems: List<FocusBoardItemWithTags>,
    trackedActivities: List<TrackedActivityRecentOverview> = emptyList(),
    tags: List<FocusBoardItemTag>,
    onTagToggle: (FocusBoardItemTag) -> Unit,
    swapFocusItems: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onCompleteToggle: (FocusBoardItemWithTags) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        swapFocusItems(from, to)
    }

    if (focusItems.isEmpty() && trackedActivities.isEmpty()) {
        EmptyState(
            icon = Icons.AutoMirrored.Outlined.FactCheck,
            title = stringResource(id = R.string.focus_board_no_focus_items),
            description = stringResource(id = R.string.focus_board_empty_desc)
        )
    } else {
        val pinned = focusItems.filter { it.item.isPinned }
        val active = focusItems.filter { !it.item.isPinned && !it.item.isCompleted }
        val completed = focusItems.filter { it.item.isCompleted && !it.item.isPinned }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {

            if (trackedActivities.isNotEmpty()) {
                items(trackedActivities, key = { "activity_${it.activity.id}" }) { activity ->
                    TrackedActivity(
                        item = activity,
                        onNavigate = {},
                        onActionButtonClick = {},
                        onAddRecord = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (tags.isNotEmpty()) {
                item(key = "tags_header") {
                    HeaderWithTags(
                        onToggle = onTagToggle,
                        tags = tags,
                    )
                }
            }

            if (pinned.isNotEmpty()) {
                item(key = "pinned_header") {
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.focus_board_pinned), style = MaterialTheme.typography.titleSmall, color = AppTheme.colors.onSurfaceVariant)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(16.dp), tint = AppTheme.colors.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                itemsIndexed(pinned, { _, item -> item.item.id }) { index, item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.item.id
                    ) { isDragging ->
                        Column {
                            FocusBoardItem(
                                item = item,
                                onClick = { onFocusItemClick(item) },
                                onCompleteToggle = { onCompleteToggle(item) },
                                isDragging = isDragging,
                                shape = getGroupedShape(index, pinned.size),
                                modifier = Modifier.longPressDraggableHandle()
                            )
                            if (index < pinned.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                )
                            }
                        }
                    }
                }

                item(key = "pinned_spacer") { Spacer(modifier = Modifier
                    .height(24.dp)
                    .animateItem()) }
            }

            itemsIndexed(active, { _, item -> item.item.id }) { index, item ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = item.item.id
                ) { isDragging ->
                    Column {
                        FocusBoardItem(
                            item = item,
                            onClick = { onFocusItemClick(item) },
                            onCompleteToggle = { onCompleteToggle(item) },
                            isDragging = isDragging,
                            shape = getGroupedShape(index, active.size),
                            modifier = Modifier.longPressDraggableHandle()
                        )
                        if (index < active.size - 1) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                            )
                        }
                    }
                }
            }

            if (completed.isNotEmpty()) {
                item(key = "completed_header") {
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .padding(start = 4.dp, end = 4.dp, top = 24.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.daily_checklist_completed), style = MaterialTheme.typography.titleSmall, color = AppTheme.colors.onSurfaceVariant)
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = CircleShape,
                            color = AppTheme.colors.surfaceVariant,
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                text = completed.size.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                itemsIndexed(completed, { _, item -> item.item.id }) { index, item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.item.id
                    ) { isDragging ->
                        Column {
                            FocusBoardItem(
                                item = item,
                                onClick = { onFocusItemClick(item) },
                                onCompleteToggle = { onCompleteToggle(item) },
                                isDragging = isDragging,
                                shape = getGroupedShape(index, completed.size),
                                modifier = Modifier.longPressDraggableHandle()
                            )
                            if (index < completed.size - 1) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                )
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun getGroupedShape(index: Int, total: Int): Shape {
    val topStart by animateDpAsState(targetValue = if (total == 1 || index == 0) 24.dp else 4.dp, label = "topStart")
    val topEnd by animateDpAsState(targetValue = if (total == 1 || index == 0) 24.dp else 4.dp, label = "topEnd")
    val bottomStart by animateDpAsState(targetValue = if (total == 1 || index == total - 1) 24.dp else 4.dp, label = "bottomStart")
    val bottomEnd by animateDpAsState(targetValue = if (total == 1 || index == total - 1) 24.dp else 4.dp, label = "bottomEnd")

    return RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd
    )
}

@Composable
fun FocusBoardItem(
    item: FocusBoardItemWithTags,
    onClick: () -> Unit,
    onCompleteToggle: () -> Unit,
    isDragging: Boolean,
    shape: Shape = RoundedCornerShape(24.dp),
    modifier: Modifier = Modifier,
) {
    val isCheckable = item.tags.any { it.isTaskTag }
    val mainTag = item.getMainTag()
    val baseColor = mainTag?.getUIColor()?.harmonizeWithTheme() ?: AppTheme.colors.surfaceVariant

    Surface(
        shape = shape,
        modifier = modifier.fillMaxWidth(),
        color = if (isDragging) baseColor.darker(0.05f) else baseColor,
        shadowElevation = if (isDragging) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isCheckable) {
                IconButton(
                    onClick = onCompleteToggle,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (item.item.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = "Complete task",
                        tint = if (item.item.isCompleted) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (item.tags.size >= 2) {
                    Row(
                        modifier = Modifier.padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item.tags.drop(1).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = tag.color.toThemeColor().darker(0.2f),
                            ) {
                                Text(
                                    text = tag.name,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                }

                if (item.item.title.isNotEmpty()) {
                    Text(
                        item.item.title,
                        style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppTheme.colors.onSurfaceTitle,
                            textDecoration = if (item.item.isCompleted) TextDecoration.LineThrough else null
                        ),
                    )
                }

                if (item.item.content.isNotEmpty()) {
                    RichContentEditor(
                        state = rememberRichContentEditorState(item.item.content.value),
                        isReadOnly = true
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = stringResource(id = R.string.screen_group_reorder),
                tint = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}