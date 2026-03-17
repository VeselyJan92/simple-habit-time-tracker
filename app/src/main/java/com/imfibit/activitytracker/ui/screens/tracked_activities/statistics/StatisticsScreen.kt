package com.imfibit.activitytracker.ui.screens.tracked_activities.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Score
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.core.extensions.sumByLong
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.dialogs.system.DatePickerDialog
import com.imfibit.activitytracker.ui.components.topBar.SimpleBackTopBar
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.Goal
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun ScreenStatistics() {
    val navigation = hiltViewModel<BackstackViewModel>()
    val vm = hiltViewModel<StatisticsViewModel>()

    ScreenStatisticsContent(
        onBack = { navigation.popBackStack() },
        getPageData = { start, end, range -> 
            vm.getPageData(start, end, range)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenStatisticsContent(
    onBack: () -> Unit,
    getPageData: suspend (LocalDate, LocalDate, TimeRange) -> Map<TrackedActivity.Type, List<ActivityWithMetric>>
) {
    Scaffold(
        topBar = {
            SimpleBackTopBar(
                title = stringResource(id = R.string.screen_title_statistics),
                onBack = onBack
            )
        },
        containerColor = AppTheme.colors.lightBackground // Clean dashboard background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val origin = remember { mutableStateOf(now) }
            val range = remember { mutableStateOf(TimeRange.WEEKLY) }
            val date = remember { mutableStateOf(now) }

            val pagerState = rememberPagerState(
                initialPage = 51,
                initialPageOffsetFraction = 0f,
                pageCount = { 100 }
            )

            val scope = rememberCoroutineScope()

            Navigation(
                range = range.value,
                date = date.value,
                goTo = {
                    scope.launch {
                        origin.value = it
                        date.value = it
                        pagerState.scrollToPage(51)
                    }
                },
                setRange = {
                    scope.launch {
                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        origin.value = today
                        date.value = today
                        range.value = it
                        pagerState.scrollToPage(51)
                    }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Top
            ) { page ->

                val relativePage = (51 - page).toLong()

                val interval = range.value.getBoundaries(
                    when (range.value) {
                        TimeRange.DAILY -> origin.value.minus(relativePage, DateTimeUnit.DAY)
                        TimeRange.WEEKLY -> origin.value.minus(relativePage, DateTimeUnit.WEEK)
                        TimeRange.MONTHLY -> origin.value.minus(relativePage, DateTimeUnit.MONTH)
                    }
                )

                Column {
                    val keys = arrayOf<Any>(range.value, origin.value, date.value, page)

                    val data = remember(*keys) {
                        mutableStateOf(mapOf<TrackedActivity.Type, List<ActivityWithMetric>>())
                    }

                    LaunchedEffect(*keys) {
                        data.value = getPageData(interval.first, interval.second, range.value)
                    }

                    if (data.value.isEmpty()) {
                        EmptyData(range.value, interval.first)
                    } else {
                        Surface(
                            modifier = Modifier.padding(top = 16.dp),
                            shadowElevation = 0.dp,
                            color = Color.Transparent
                        ) {
                            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                                NavigationHeader(
                                    range = range.value,
                                    rangeDate = interval.first,
                                )

                                Spacer(Modifier.height(8.dp))

                                BlockTimeTracked(data.value[TrackedActivity.Type.TIME], range.value)
                                BlockScores(data.value[TrackedActivity.Type.SCORE], range.value)
                                BlockCompleted(
                                    data.value[TrackedActivity.Type.CHECKED],
                                    range.value,
                                    date.value
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
private fun EmptyData(
    range: TimeRange,
    date: LocalDate,
) {
    Surface(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(300.dp),
        shadowElevation = 0.dp,
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.7f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            NavigationTitle(
                range = range,
                rangeDate = date,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowLeft, 
                    contentDescription = null,
                    tint = AppTheme.colors.outline
                )

                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(bottom = 12.dp),
                        imageVector = Icons.Outlined.Analytics,
                        tint = AppTheme.colors.outlineVariant,
                        contentDescription = "Focus item"
                    )

                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = stringResource(id = R.string.no_records),
                        color = AppTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp
                    )

                    Text(
                        text = stringResource(id = R.string.statistics_swipe),
                        color = AppTheme.colors.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowRight, 
                    contentDescription = null,
                    tint = AppTheme.colors.outline
                )
            }
        }
    }
}

@Composable
private fun Navigation(
    range: TimeRange,
    date: LocalDate,
    goTo: (LocalDate) -> Unit,
    setRange: (TimeRange) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        shadowElevation = 0.dp,
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
        shape = RoundedCornerShape(50) // Pill shape
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeRange.values().forEach { timeRange ->
                val isSelected = range == timeRange
                val bgColor = if (isSelected) AppTheme.colors.primary else Color.Transparent
                val textColor = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onSurfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .background(bgColor, RoundedCornerShape(50))
                        .clickable { setRange(timeRange) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = timeRange.label).uppercase(),
                        color = textColor,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            var showDatePicker by remember { mutableStateOf(false) }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    date = date,
                    onDatePicked = {
                        goTo(it ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AppTheme.colors.surfaceVariant, CircleShape)
                    .clickable { showDatePicker = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CalendarToday, 
                    contentDescription = null,
                    tint = AppTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationHeader(
    range: TimeRange,
    rangeDate: LocalDate,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowLeft, 
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.weight(1f))

        NavigationTitle(range, rangeDate)

        Spacer(Modifier.weight(1f))

        Icon(
            Icons.AutoMirrored.Filled.ArrowRight, 
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun NavigationTitle(
    range: TimeRange,
    rangeDate: LocalDate,
) {
    Text(
        text = range.getDateLabel(rangeDate).value(),
        color = AppTheme.colors.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun Header(title: String, icon: ImageVector, last: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.padding(bottom = 12.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = AppTheme.colors.iconBackground,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, 
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = AppTheme.colors.onSurface,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        last?.invoke()
    }
}

@Composable
private fun BlockTimeTracked(
    data: List<ActivityWithMetric>?,
    range: TimeRange,
) {
    if (data.isNullOrEmpty()) return

    Surface(
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
    ) {
        Column(Modifier.padding(16.dp)) {
            Header(title = stringResource(id = R.string.time), icon = Icons.Default.Timer) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppTheme.colors.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val sum = TrackedActivity.Type.TIME.getLabel(data.sumByLong { it.metric }).value()

                        Text(
                            sum, 
                            color = AppTheme.colors.onSurfaceVariant,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            data.forEachIndexed { index, it ->
                Row(
                    Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.activity.name, 
                        color = AppTheme.colors.onSurfaceTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range) {
                        Goal(label = it.activity.formatGoal())
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    val color = getColor(it, range)

                    BaseMetricBlock(
                        metric = it.activity.type.getLabel(it.metric).value(),
                        color = color,
                        metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                if (index != data.size - 1) {
                    HorizontalDivider(color = AppTheme.colors.divider.copy(alpha = 0.5f))
                }
            }
        }
    }
}


@Composable
private fun BlockScores(
    data: List<ActivityWithMetric>?,
    range: TimeRange,
) {
    if (data.isNullOrEmpty()) return

    Surface(
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
    ) {
        Column(Modifier.padding(16.dp)) {

            Header(title = stringResource(id = R.string.score), Icons.Default.Score)

            data.forEachIndexed { index, it ->
                Row(
                    Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.activity.name, 
                        color = AppTheme.colors.onSurfaceTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range) {
                        Goal(label = it.activity.formatGoal())
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    BaseMetricBlock(
                        metric = it.metric.toString(),
                        color = getColor(it, range),
                        metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                if (index != data.size - 1) {
                    HorizontalDivider(color = AppTheme.colors.divider.copy(alpha = 0.5f))
                }
            }
        }
    }
}


@Composable
private fun BlockCompleted(
    data: List<ActivityWithMetric>?,
    range: TimeRange,
    date: LocalDate,
) {
    if (data.isNullOrEmpty()) return

    Surface(
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
    ) {
        Column(Modifier.padding(16.dp)) {

            Header(title = stringResource(id = R.string.habits), Icons.Default.AssignmentTurnedIn)

            data.forEachIndexed { index, it ->
                Row(
                    Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.activity.name, 
                        color = AppTheme.colors.onSurfaceTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (it.activity.isGoalSet() && it.activity.goal.range == range && it.activity.goal.range != TimeRange.DAILY) {
                        Goal(label = it.activity.formatGoal())
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    val label =
                        if (it.activity.type == TrackedActivity.Type.CHECKED && range == TimeRange.DAILY)
                            stringResource(id = R.string.yes).uppercase()
                        else
                            "${it.metric} / ${range.getNumberOfDays(date)}"

                    BaseMetricBlock(
                        metric = label,
                        color = getColor(it, range), 
                        metricStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                if (index != data.size - 1) {
                    HorizontalDivider(color = AppTheme.colors.divider.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun getColor(item: ActivityWithMetric, range: TimeRange): Color {
    return if (item.activity.goal.range == range && item.metric < item.activity.goal.value) {
        AppTheme.colors.surfaceVariant // Matches "missed" muted grey
    } else {
        AppTheme.colors.primary // Matches primary deep purple accent
    }
}


@Preview(showBackground = true)
@Composable
fun ScreenStatisticsPreview() = AppTheme {
    ScreenStatisticsContent(
        onBack = {},
        getPageData = { _, _, _ ->
            mapOf(
                TrackedActivity.Type.TIME to listOf(
                    ActivityWithMetric(
                        activity = TrackedActivity(
                            id = 1,
                            name = "Reading",
                            type = TrackedActivity.Type.TIME,
                            position = 0,
                            inSessionSince = null,
                            goal = TrackedActivityGoal(3600L, TimeRange.DAILY),
                            challenge = TrackedActivityChallenge.empty
                        ),
                        metric = 1800L
                    ),
                    ActivityWithMetric(
                        activity = TrackedActivity(
                            id = 2,
                            name = "Coding",
                            type = TrackedActivity.Type.TIME,
                            position = 1,
                            inSessionSince = null,
                            goal = TrackedActivityGoal(7200L, TimeRange.DAILY),
                            challenge = TrackedActivityChallenge.empty
                        ),
                        metric = 8000L
                    )
                ),
                TrackedActivity.Type.SCORE to listOf(
                    ActivityWithMetric(
                        activity = TrackedActivity(
                            id = 3,
                            name = "Pushups",
                            type = TrackedActivity.Type.SCORE,
                            position = 2,
                            inSessionSince = null,
                            goal = TrackedActivityGoal(50L, TimeRange.DAILY),
                            challenge = TrackedActivityChallenge.empty
                        ),
                        metric = 20L
                    )
                ),
                TrackedActivity.Type.CHECKED to listOf(
                    ActivityWithMetric(
                        activity = TrackedActivity(
                            id = 4,
                            name = "Vitamins",
                            type = TrackedActivity.Type.CHECKED,
                            position = 3,
                            inSessionSince = null,
                            goal = TrackedActivityGoal(1L, TimeRange.DAILY),
                            challenge = TrackedActivityChallenge.empty
                        ),
                        metric = 1L
                    )
                )
            )
        }
    )
}
