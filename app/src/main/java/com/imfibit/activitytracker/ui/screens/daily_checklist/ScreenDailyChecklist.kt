package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.DashboardBody
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors
import com.imfibit.activitytracker.ui.components.darker
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import kotlin.math.max

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
    add(
        DailyChecklistItem(
            title = "Make time for workout",
            description = "Make time for workout",
            color = chooseableColors.random().toArgb(),
            id = 3
        )
    )
    add(
        DailyChecklistItem(
            title = "This random shit",
            description = "",
            id = 4,
            color = chooseableColors.random().toArgb(),
        )
    )
}


@Preview
@Composable
private fun Preview() = AppTheme {
    Body(
        items = items,
        days = buildList {
            repeat(30) {
                add(DailyChecklistTimelineItemValue(LocalDate.now().minusDays(it.toLong()), true))
            }
        },
        history = listOf(),
        strike = 7,
        onCheckItem = { checked, item -> },
        onToggleDay = { checked, item -> },
        onItemEdit = {},
        onItemDelete = {},
        onSwap = { x, y -> }
    )
}


@Composable
fun ScreenMindBoot() {
    val viewModel = hiltViewModel<DailyChecklistViewModel>()

    val items by viewModel.items.collectAsState()
    val days by viewModel.days.collectAsState()
    val strike by viewModel.strike.collectAsState()
    val history by viewModel.history.collectAsState()

    Body(
        items = items,
        days = days,
        history = history,
        strike = strike,
        onCheckItem = viewModel::onCheck,
        onToggleDay = viewModel::onToggleDay,
        onItemEdit = viewModel::onEdit,
        onItemDelete = viewModel::onDelete,
        onSwap = viewModel::onSwap,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Body(
    items: List<DailyChecklistItem>,
    days: List<DailyChecklistTimelineItemValue>,
    history: List<DailyChecklistTimelineItemValue>,
    strike: Int,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
) {
    var showHistoryBottomSheet by remember { mutableStateOf(false) }



    if (showHistoryBottomSheet) {
        DailyChecklistHistoryBottomSheet(
            history = history,
            onToggleDay = onToggleDay,
            onDismissRequest = {
                showHistoryBottomSheet = false
            }
        )
    }


    DashboardBody {
        TopBar(
            onCalendarClicked = {
                showHistoryBottomSheet = true
            }
        )

        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .testTag(TestTag.DAILY_CHECKLIST_EMPTY_SECTION)
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
                    imageVector = Icons.Default.Checklist,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = stringResource(R.string.daily_checklist_empty_title),
                    fontWeight = FontWeight.Bold, fontSize = 18.sp
                )

                Text(
                    text = stringResource(R.string.daily_checklist_empty_message),
                    textAlign = TextAlign.Center
                )

            }
        } else {
            DailyChecklist(
                items,
                days,
                strike,
                onCheckItem,
                onToggleDay,
                onItemEdit,
                onItemDelete,
                onSwap,
            )
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
        shape = RoundedCornerShape(15.dp),
        shadowElevation = 2.dp,
        color = Colors.SuperLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp) // Increased padding for more breathing room
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
                                fontSize = 20.sp
                            )
                        ) { // Larger font for strike
                            append("$strike")
                        }
                        append(" ")
                        append(stringResource(R.string.days))
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 48.dp)
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
                        .background(if (it.completed) Colors.ButtonGreen else Colors.ChipGray)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleDay(!it.completed, it.date_completed)
                        }
                )
            }
        }
    ) { measurables, constraints ->
        val boxSize = 16.dp.roundToPx()
        val spacing = 4.dp.roundToPx()

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

        var currentWidth = 0
        val itemsToPlace = mutableListOf<Placeable>()

        for (placeable in placeables) {
            // Check if adding the current item exceeds the max width
            if (currentWidth + placeable.width <= constraints.maxWidth) {
                itemsToPlace.add(0, placeable) // Add to the beginning to maintain the order
                currentWidth += placeable.width + spacing
            } else {
                break // Stop when adding the next item would exceed the width
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

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .testTag(TestTag.DAILY_CHECKLIST_LIST)
            .fillMaxHeight(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item(key = "header") {
            TopOverview(days, strike, onToggleDay)
        }

        items(items, key = { it.id }) { item ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = item.id
            ) { isDragging ->
                DailyChecklistItem(
                    modifier = Modifier.longPressDraggableHandle(),
                    item = item,
                    dragging = isDragging,
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

@Composable
private fun TopBar(onCalendarClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Daily checklist",
            fontWeight = FontWeight.Black, fontSize = 25.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onCalendarClicked
        ) {
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyChecklistItem(
    modifier: Modifier = Modifier,
    item: DailyChecklistItem,
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
        shape = RoundedCornerShape(15.dp),
        color = Color(item.color).let { if (dragging) it.darker(0.25f) else it },
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
            ) {

                if (item.title.isNotBlank()) {
                    Text(
                        style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        text = item.title
                    )
                }

                if (item.description.isNotBlank()) {
                    Text(
                        style = TextStyle.Default.copy(
                            fontSize = 15.sp
                        ),
                        modifier = Modifier,
                        text = item.description
                    )
                }

            }

            val now = remember { LocalDate.now() }

            val haptic = LocalHapticFeedback.current

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    modifier = Modifier.testTag(TestTag.CHECKBOX),
                    checked = item.date_checked == now,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCheckItem(it, item)
                    }
                )
            }

        }
    }
}
