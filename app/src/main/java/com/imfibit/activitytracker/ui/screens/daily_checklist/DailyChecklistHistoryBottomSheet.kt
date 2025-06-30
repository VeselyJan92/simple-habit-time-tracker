package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.DateUtils
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItemValue
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MonthSplitter
import com.imfibit.activitytracker.ui.components.metricTextStyle
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewDailyChecklistHistoryBottomSheet() {
    val sampleHistory = buildList {
        val today = LocalDate.now()
        add(DailyChecklistTimelineItemValue(today, true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(1).withDayOfMonth(15), true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(2).withDayOfMonth(10), false))
        add(DailyChecklistTimelineItemValue(today.minusMonths(3).withDayOfMonth(6), true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(4).withDayOfMonth(7), true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(5).withDayOfMonth(8), true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(6).withDayOfMonth(9), true))
        add(DailyChecklistTimelineItemValue(today.minusMonths(7).withDayOfMonth(10), true))
    }
    AppTheme {
        DailyChecklistHistoryBottomSheet(
            state = rememberTestBottomSheetState(),
            history = sampleHistory,
            onToggleDay = { _, _ -> },
            onDismissRequest = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChecklistHistoryBottomSheet(
    state: SheetState = rememberAppBottomSheetState(),
    history: List<DailyChecklistTimelineItemValue>,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) = BaseBottomSheet(
    state = state,
    onDismissRequest = onDismissRequest,
    content = { _ ->
        DailyChecklistHistoryContent(
            history = history,
            onToggleDay = onToggleDay,
        )
    }
)

@Composable
private fun DailyChecklistHistoryContent(
    history: List<DailyChecklistTimelineItemValue>,
    onToggleDay: (checked: Boolean, date: LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DayOfWeek.entries.forEach {
            Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = it.getDisplayName(
                        java.time.format.TextStyle.SHORT,
                        Locale.getDefault()
                    ).uppercase(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp
                )
            }
        }
    }

    val groupedByMonth = remember(history) {
        history.groupBy { item -> item.date_completed.withDayOfMonth(1) }.toList()
    }

    LazyColumn(
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = groupedByMonth, key = { it.first }) {
            Column {
                val month = YearMonth.of(it.first.year, it.first.monthValue)

                MonthSplitter(month)

                Month(
                    month = month,
                    items = it.second.associateBy { item -> item.date_completed },
                    onDayClicked = onToggleDay
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
private fun Month(
    month: YearMonth,
    onDayClicked: (checked: Boolean, date: LocalDate) -> Unit,
    items: Map<LocalDate, DailyChecklistTimelineItemValue>,
) = Layout(
    content = {
        val weeks = remember(month, items) {
            DateUtils.getWeeksInMonth(month)
        }

        weeks.forEach { week ->
            // Day content
            week.forEach { day ->
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    if (day.month == month.month) {
                        val dayModifier = if (day == LocalDate.now())
                            Modifier
                                .border(width = 2.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                        else
                            Modifier

                        val color = if (items[day]?.completed
                                ?: false
                        ) Colors.ButtonGreen else Colors.ChipGray

                        Box(
                            modifier = dayModifier
                                .size(40.dp, 25.dp)
                                .background(color, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    onClick = {
                                        onDayClicked(
                                            !(items[day]?.completed ?: false),
                                            day
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                modifier = Modifier.align(Alignment.Center),
                                style = metricTextStyle
                            )
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .width(40.dp)
                                .height(25.dp)
                        )
                    }
                }
            }
        }
    }) { measurables, constraints ->

    val placeables = measurables.chunked(7).map { it.map { it.measure(constraints) } }

    val rowHeight = placeables.first().first().height
    val totalHeight = rowHeight * placeables.size

    layout(constraints.maxWidth, totalHeight) {
        var yPos = 0

        placeables.forEach { dayPlaceables ->
            val totalItemWidth = dayPlaceables.sumOf { it.width }
            val remainingSpace = constraints.maxWidth - totalItemWidth
            val padding = remainingSpace / 6.0

            var xPos = 0.0

            dayPlaceables.forEach { day ->
                day.placeRelative(xPos.toInt(), yPos)
                xPos += (padding + day.width)
            }

            yPos += rowHeight
        }
    }
}