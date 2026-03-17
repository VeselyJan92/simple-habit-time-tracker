package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.DashboardBody
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors
import com.imfibit.activitytracker.ui.components.darker
import com.imfibit.activitytracker.core.extensions.toThemeColor
import com.imfibit.activitytracker.ui.components.EmptyState
import com.imfibit.activitytracker.ui.components.EmptyStateHint
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.max

@Composable
fun ScreenDailyChecklist() {
    val viewModel = hiltViewModel<DailyChecklistViewModel>()

    val data by viewModel.data.collectAsStateWithLifecycle()

    ScreenDailyChecklistContent(
        data = data,
        onCheckItem = viewModel::onCheck,
        onToggleDay = viewModel::onToggleDay,
        onItemEdit = viewModel::onEdit,
        onItemDelete = viewModel::onDelete,
        onSwap = viewModel::onSwap,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenDailyChecklistContent(
    data: DailyChecklistViewModel.Data?,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
) {
    var showHistoryBottomSheet by remember { mutableStateOf(false) }

    if (showHistoryBottomSheet) {
        DailyChecklistHistoryBottomSheet(
            history = data?.history ?: emptyList(),
            onToggleDay = onToggleDay,
            onDismissRequest = {
                showHistoryBottomSheet = false
            }
        )
    }

    Surface(color = Color.Transparent) {
        DashboardBody {
            TopBar(
                onCalendarClicked = {
                    showHistoryBottomSheet = true
                }
            )

            if (data != null) {
                if (data.items.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.testTag(TestTag.DAILY_CHECKLIST_EMPTY_SECTION),
                        icon = Icons.Default.Checklist,
                        title = stringResource(id = R.string.daily_checklist_empty_title),
                        description = stringResource(id = R.string.daily_checklist_empty_message),
                    )
                } else {
                    DailyChecklist(
                        data.items,
                        data.days,
                        data.strike,
                        onCheckItem,
                        onToggleDay,
                        onItemEdit,
                        onItemDelete,
                        onSwap,
                    )
                }
            }
        }
    }
}

@Composable
fun TopOverview(
    days: List<DailyChecklistTimelineItemValue>,
    strike: Int,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp), // Smaller corners for the indicator card
    ) {
        Column(
            modifier = Modifier.background(color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.75f))
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prominent strike count
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = AppTheme.colors.onSurface
                            )
                        ) {
                            append("$strike")
                        }
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = AppTheme.colors.outline
                            )
                        ) {
                            append(" ")
                            append(stringResource(R.string.days))
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp)
                )

                TruncatingBoxRow(days, onToggleDay)
            }
        }
    }
}

@Composable
fun TruncatingBoxRow(
    days: List<DailyChecklistTimelineItemValue>,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Layout(
        content = {
            days.forEach {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (it.completed) AppTheme.colors.success else AppTheme.colors.surfaceVariant)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleDay(!it.completed, it.date_completed)
                        }
                )
            }
        }
    ) { measurables, constraints ->
        val boxSize = 16.dp.roundToPx()
        val spacing = 6.dp.roundToPx()

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

        var currentWidth = 0
        val itemsToPlace = mutableListOf<Placeable>()

        for (placeable in placeables) {
            if (currentWidth + placeable.width <= constraints.maxWidth) {
                itemsToPlace.add(0, placeable)
                currentWidth += placeable.width + spacing
            } else {
                break
            }
        }

        layout(max(currentWidth - spacing, 0), boxSize) {
            var xPosition = 0
            itemsToPlace.forEach { placeable ->
                placeable.placeRelative(xPosition, 0)
                xPosition += placeable.width + spacing
            }
        }
    }
}

@Composable
fun DailyChecklist(
    items: List<DailyChecklistItem>,
    days: List<DailyChecklistTimelineItemValue>,
    strike: Int,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
) {

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onSwap(from, to)
    }

    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val completedItems = items.filter { it.date_checked == now }
    val activeItems = items.filter { it.date_checked != now }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .testTag(TestTag.DAILY_CHECKLIST_LIST)
            .fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item(key = "header") {
            TopOverview(days, strike, onToggleDay)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Active Items
        items(activeItems, key = { it.id }) { item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = item.id
            ) { isDragging ->
                DailyChecklistItem(
                    modifier = Modifier.longPressDraggableHandle(),
                    item = item,
                    isChecked = false,
                    dragging = isDragging,
                    onCheckItem = onCheckItem,
                    onItemEdit = onItemEdit,
                    onItemDelete = onItemDelete
                )
            }
        }

        // Completed Section
        if (completedItems.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.daily_checklist_completed),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = CircleShape,
                        color = AppTheme.colors.surfaceContainerLow,
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = completedItems.size.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.onSurface, // Very dark
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(id = R.string.daily_checklist_reset),
                        tint = AppTheme.colors.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            items(completedItems, key = { it.id }) { item ->
                DailyChecklistItem(
                    item = item,
                    isChecked = true,
                    dragging = false,
                    onCheckItem = onCheckItem,
                    onItemEdit = onItemEdit,
                    onItemDelete = onItemDelete
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onCalendarClicked: () -> Unit) {
    TopAppBar(
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text(
                text = stringResource(id = R.string.daily_checklist),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = AppTheme.colors.onSurface // Strongest contrast
            )
        },
        actions = {
            Surface(
                shape = CircleShape,
                color = AppTheme.colors.surfaceContainerLow,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
            ) {
                IconButton(onClick = onCalendarClicked) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = stringResource(id = R.string.screen_title_record_history),
                        tint = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyChecklistItem(
    modifier: Modifier = Modifier,
    item: DailyChecklistItem,
    isChecked: Boolean,
    dragging: Boolean,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
) {
    var dialogDailyChecklist by remember { mutableStateOf(false) }

    if (dialogDailyChecklist) {
        EditDailyChecklistItemBottomSheet(
            onDismissRequest = {
                dialogDailyChecklist = false
            },
            isEdit = true,
            item = item,
            onItemEdit = onItemEdit,
            onItemDelete = onItemDelete
        )
    }

    val baseColor = item.color.toThemeColor()
    val haptic = LocalHapticFeedback.current

    // Make the entire card heavily saturated like the user requested.
    val backgroundColor = when {
        dragging -> baseColor.darker(0.1f)
        isChecked -> baseColor.copy(alpha = 0.6f)
        else -> baseColor.copy(alpha = 0.6f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                dialogDailyChecklist = true
            }
            .testTag(TestTag.DAILY_CHECKLIST_LIST_ITEM),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = if (dragging) 4.dp else 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = item.title,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AppTheme.colors.onSurface,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = TextStyle.Default.copy(
                            fontSize = 14.sp,
                            color = AppTheme.colors.onSurfaceVariant,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Simple transparent click box on the right
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCheckItem(!isChecked, item)
                    }
            ) {
                Icon(
                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Complete",
                    tint = if (isChecked) AppTheme.colors.outline else baseColor.darker(0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}

@Preview
@Composable
private fun ScreenDailyChecklistPreview() = AppTheme {
    val items = buildList {
        add(
            DailyChecklistItem(
                title = "Random thing",
                description = "Random thing",
                color = chooseableColors.random().toArgb(),
                id = 1
            )
        )
        add(
            DailyChecklistItem(
                title = "Most important things first",
                description = "Most important things first, Customize Toolbar…, Customize Toolbar…",
                color = chooseableColors.random().toArgb(),
                id = 2
            )
        )
    }

    ScreenDailyChecklistContent(
        data = DailyChecklistViewModel.Data(
            items = items,
            days = buildList {
                repeat(30) {
                    add(DailyChecklistTimelineItemValue(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(it.toLong(), DateTimeUnit.DAY), true))
                }
            },
            history = listOf(),
            strike = 7
        ),
        onCheckItem = { checked, item -> },
        onToggleDay = { checked, item -> },
        onItemEdit = {},
        onItemDelete = {},
        onSwap = { x, y -> }
    )
}

@Preview
@Composable
private fun ScreenDailyChecklistEmptyPreview() = AppTheme {
    ScreenDailyChecklistContent(
        data = DailyChecklistViewModel.Data(
            items = emptyList(),
            days = emptyList(),
            history = emptyList(),
            strike = 0
        ),
        onCheckItem = { _, _ -> },
        onToggleDay = { _, _ -> },
        onItemEdit = {},
        onItemDelete = {},
        onSwap = { _, _ -> }
    )
}
