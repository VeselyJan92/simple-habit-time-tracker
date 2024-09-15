package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.MainBody
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Colors.chooseableColors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.MonthGridWeek
import com.imfibit.activitytracker.ui.components.MonthSplitter
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import com.imfibit.activitytracker.ui.screens.focus_board.FocusItemTag
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

val items = buildList {
    add(DailyChecklistItem(
        title = "Random thing",
        description = "Random thing",
        color = chooseableColors.random().toArgb(),
        id = 1
    ))
    add(DailyChecklistItem(
            title = "Most important things first",
            description = "Most important things first",
            color = chooseableColors.random().toArgb(),
            id = 2
    ))
    add(DailyChecklistItem(
            title = "Make time for workout",
            description = "Make time for workout",
            color = chooseableColors.random().toArgb(),
            id = 3
    ))
    add(DailyChecklistItem(
        title = "This random shit",
        description = "This random shit",
        id = 4,
        color = chooseableColors.random().toArgb(),
    ))
}

val months = buildList {
    add(DevSeeder.getMonthData(YearMonth.now()))
    add(DevSeeder.getMonthData(YearMonth.now()))
    add(DevSeeder.getMonthData(YearMonth.now()))
}

@Preview
@Composable
private fun Preview() {
    Body(
        items = items,
        months = months,
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
    val months by viewModel.months.collectAsState()

    Body(
        items = items,
        months = months,
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
    months: List<RepositoryTrackedActivity.Month>,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: ItemPosition, to: ItemPosition) -> Unit,
    onReordered: () -> Unit
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
                months,
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
fun DailyChecklist(
    items: List<DailyChecklistItem>,
    months: List<RepositoryTrackedActivity.Month>,
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onSwap: (from: ItemPosition, to: ItemPosition) -> Unit,
    onReordered: () -> Unit
) {


    val state = rememberReorderableLazyListState(
        onDragEnd = { from, to -> onReordered() },
        onMove = onSwap,
        canDragOver = { draggedOver, dragging -> draggedOver.index < items.size }
    )

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .testTag(TestTag.DAILY_CHECKLIST_LIST)
            .reorderable(state = state)
            .detectReorderAfterLongPress(state),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(items, key = { it.id }) { item ->
            DailyChecklistItem(item, onCheckItem, onItemEdit, onItemDelete)
        }

        item {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Recent activity",
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )
        }


        items(months) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(15.dp),
                elevation = 1.dp,
                color = Colors.SuperLight

            ) {
                MindBootMonth(
                    modifier = Modifier.padding(8.dp),
                    month = item,
                    onToggleDay = onToggleDay
                )
            }
        }
    }
}


@Composable
fun MindBootMonth(
    modifier: Modifier = Modifier,
    month: RepositoryTrackedActivity.Month,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit
) = Column {
    MonthSplitter(month = month.month)

    Column(modifier = modifier.padding(3.dp)) {
        month.weeks.forEach {
            MonthGridWeek(
                week = it,
                month = month.month.month,
                weekSum = {
                    MetricBlock(
                        data = MetricWidgetData(
                            value = { "${it.total}/7" },
                            color = Colors.ChipGray,
                            label = { resources.getString(R.string.week) }
                        )
                    )
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
    }
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
    onCheckItem: (checked: Boolean, item: DailyChecklistItem) -> Unit,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit
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
            .clickable {
                show.value = true
            }
            .testTag(TestTag.DAILY_CHECKLIST_LIST_ITEM),
        shape = RoundedCornerShape(15.dp),
        color = Color(item.color),
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
