package com.imfibit.activitytracker.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITIES_PAGER_ID
import com.imfibit.activitytracker.ui.SCREEN_FOCUS_BOARD_PAGER_ID
import com.imfibit.activitytracker.ui.SCREEN_DAILY_CEHCKLIST_ID
import com.imfibit.activitytracker.ui.components.ActivityFab
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.ScreenTrackedActivities
import com.imfibit.activitytracker.ui.screens.daily_checklist.EditDailyChecklistItemBottomSheet
import com.imfibit.activitytracker.ui.screens.daily_checklist.ScreenDailyChecklist
import com.imfibit.activitytracker.ui.screens.focus_board.components.BottomSheetBundleSettings
import com.imfibit.activitytracker.ui.screens.focus_board.ScreenFocusBoard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val dashboardViewModel = hiltViewModel<DashboardViewModel>()

    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = { 3 }
    )

    var addDailyChecklist by remember { mutableStateOf(false) }
    if (addDailyChecklist) {
        EditDailyChecklistItemBottomSheet(
            onDismissRequest = { addDailyChecklist = false },
            isEdit = false,
            item = DailyChecklistItem(
                title = "",
                color = Colors.chooseableColors[0].toArgb(),
                description = ""
            ),
            onItemEdit = { item -> dashboardViewModel.addDailyChecklistItem(item = item) }
        )
    }

    var addBundle by remember { mutableStateOf(false) }
    if (addBundle) {
        BottomSheetBundleSettings(
            onDismissRequest = { addBundle = false },
            bundle = FocusBundle(title = "", color = Colors.chooseableColors.first().toArgb().toLong()),
            onBundleSave = { bundle, tags ->
                dashboardViewModel.createNewBundle(bundle, tags)
            }
        )
    }

    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background, // Match global theme background
        floatingActionButton = {
            LaunchedEffect(Unit) {
                snapshotFlow { pagerState.currentPage }.collect { fabExpanded = false }
            }

            if (pagerState.currentPage == SCREEN_ACTIVITIES_PAGER_ID) {
                ActivityFab(
                    expanded = fabExpanded,
                    onExpandedChanged = { fabExpanded = it },
                    onOptionSelected = { type ->
                        dashboardViewModel.createNewActivity("\uD83C\uDFAF Name", type)
                    },
                    onGroupSelected = {
                        dashboardViewModel.addGroup(TrackerActivityGroup(0, "New Group", 0))
                    }
                )
            } else {
                FloatingActionButton(
                    onClick = {
                        when (pagerState.currentPage) {
                            SCREEN_FOCUS_BOARD_PAGER_ID -> addBundle = true
                            SCREEN_DAILY_CEHCKLIST_ID -> addDailyChecklist = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    val icon = when (pagerState.currentPage) {
                        SCREEN_FOCUS_BOARD_PAGER_ID -> Icons.Default.Add
                        SCREEN_DAILY_CEHCKLIST_ID -> Icons.AutoMirrored.Filled.ListAlt
                        else -> Icons.Default.Add
                    }
                    Icon(icon, contentDescription = stringResource(id = R.string.dashboard_fab_add))
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(
                contentPadding = padding,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    SCREEN_ACTIVITIES_PAGER_ID -> ScreenTrackedActivities()
                    SCREEN_FOCUS_BOARD_PAGER_ID -> ScreenFocusBoard()
                    SCREEN_DAILY_CEHCKLIST_ID -> ScreenDailyChecklist()
                }
            }

            if (fabExpanded) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { fabExpanded = false }
                )
            }
        }
    }
}