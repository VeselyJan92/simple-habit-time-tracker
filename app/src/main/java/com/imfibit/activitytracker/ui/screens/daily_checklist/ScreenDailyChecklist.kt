package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.MainBody
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.MonthGridImpl
import com.imfibit.activitytracker.ui.components.darker
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            description = "Most important things first",
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
            description = "This random shit",
            id = 4,
            color = chooseableColors.random().toArgb(),
        )
    )
}


@Preview
@Composable
private fun Preview() {
    Body(
        items = items,
        days = buildList {
            repeat(7) {
                add(DailyChecklistTimelineItemValue(LocalDate.now().minusDays(it.toLong()), true))
            }
        },
        strike = 7,
        onCheckItem = { checked, item -> },
        onToggleDay = { checked, item -> },
        onItemEdit = {},
        onItemDelete = {},
        onReordered = {},
        onSwap = { x, y -> }
    )
}


@Composable
fun ScreenMindBoot() {
    val viewModel = hiltViewModel<DailyChecklistViewModel>()

    val items by viewModel.items.collectAsState()
    val days by viewModel.days.collectAsState()
    val strike by viewModel.strike.collectAsState()

    Body(
        items = items,
        days = days,
        strike = strike,
        onCheckItem = viewModel::onCheck,
        onToggleDay = viewModel::onToggleDay,
        onItemEdit = viewModel::onEdit,
        onItemDelete = viewModel::onDelete,
        onSwap = viewModel::onSwap,
        onReordered = viewModel::onReordered
    )
}

@Composable
private fun Body(
    items: List<DailyChecklistItem>,
    days: List<DailyChecklistTimelineItemValue>,
    strike: Int,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: ItemPosition, to: ItemPosition) -> Unit,
    onReordered: () -> Unit,
) {
    MainBody {
        TopBar()


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
                onReordered
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
        elevation = 2.dp,
        color = Colors.SuperLight
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()

            ) {
                Column(Modifier.fillMaxWidth()) {

                    Row(
                        Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.strike))
                            }
                            append(" $strike " + stringResource(R.string.days))
                        })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEach {
                            MetricBlock(
                                modifier = Modifier.clickable {
                                    onToggleDay(!it.completed, it.date_completed)
                                },
                                data = MetricWidgetData(
                                    value = {

                                        it.date_completed.dayOfMonth.toString().padStart(2,'0')
                                    },

                                    color = if (it.completed) Colors.ButtonGreen else Colors.ChipGray
                                )
                            )
                        }
                    }
                }
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
    onSwap: (from: ItemPosition, to: ItemPosition) -> Unit,
    onReordered: () -> Unit,
) {

    val state = rememberReorderableLazyListState(
        onDragEnd = { from, to -> onReordered() },
        onMove = onSwap,
        canDragOver = { draggedOver, dragging -> draggedOver.key != "header" }
    )

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .testTag(TestTag.DAILY_CHECKLIST_LIST)
            .reorderable(state = state),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item(key = "header") {
            TopOverview(days, strike, onToggleDay)
        }

        items(items, key = { it.id }) { item ->
            ReorderableItem(
                state = state,
                key = item.id,
                modifier = Modifier.detectReorderAfterLongPress(state),
            ) {
                DailyChecklistItem(item, it, onCheckItem, onItemEdit, onItemDelete)
            }
        }
    }
}

@Composable
private fun DailyChecklistMonth(
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    month: RepositoryTrackedActivity.Month,
) {
    MonthGridImpl(
        modifier = Modifier.padding(8.dp),
        month = month,
        weekSum = {
            MetricBlock(
                data = MetricWidgetData(
                    value = { "${it.total}/7" },
                    color = Colors.ChipGray,
                    label = { resources.getString(R.string.week) }
                )
            )
        },
        noWeekSum = {
            Spacer(Modifier.size(40.dp, 20.dp))
        },
        day = {
            MetricBlock(
                modifier = Modifier.testTag(
                    TestTag.DAILY_CHECKLIST_MONTH_GRID_DAY + it.date.format(
                        DateTimeFormatter.ISO_DATE
                    )
                ),
                data = MetricWidgetData(
                    value = it.label,
                    color = it.color,
                    label = { it.date.dayOfMonth.toString() }
                ),
                onLongClick = { onToggleDay(it.metric < 1, it.date) }
            )
        }
    )

}


@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, bottom = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Daily checklist",
            fontWeight = FontWeight.Black, fontSize = 25.sp
        )

    }

}

@Composable
private fun DailyChecklistItem(
    item: DailyChecklistItem,
    dragging: Boolean,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
) {
    val show = rememberDialog()

    DialogEditDailyChecklistItem(
        display = show,
        isEdit = true,
        item = item,
        onItemEdit = onItemEdit,
        onItemDelete = onItemDelete
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() } , indication = null) {
                show.value = true
            }
            .testTag(TestTag.DAILY_CHECKLIST_LIST_ITEM),
        shape = RoundedCornerShape(15.dp),
        color = Color(item.color).let { if (dragging) it.darker(0.25f) else it },
        elevation = 2.dp
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
                Text(
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    text = item.title
                )

                Text(
                    modifier = Modifier,
                    text = item.description
                )
            }

            val now = remember { LocalDate.now() }

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    modifier = Modifier.testTag(TestTag.CHECKBOX),
                    checked = item.date_checked == now,
                    onCheckedChange = { onCheckItem(it, item) }
                )
            }

        }
    }
}
