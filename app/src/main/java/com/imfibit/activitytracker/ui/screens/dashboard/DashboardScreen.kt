package com.imfibit.activitytracker.ui.screens.dashboard

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.screens.activity_list.ActivitiesViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITIES_PAGER_ID
import com.imfibit.activitytracker.ui.SCREEN_FOCUS_BOARD_PAGER_ID
import com.imfibit.activitytracker.ui.SCREEN_MIND_BOOT_ID
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.AddActivityBottomSheet
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.daily_checklist.DailyChecklistViewModel
import com.imfibit.activitytracker.ui.screens.daily_checklist.EditDailyChecklistItemBottomSheet
import com.imfibit.activitytracker.ui.screens.daily_checklist.ScreenMindBoot
import com.imfibit.activitytracker.ui.screens.focus_board.BottomSheetEditFocusItem
import com.imfibit.activitytracker.ui.screens.focus_board.FocusBoardViewModel
import com.imfibit.activitytracker.ui.screens.focus_board.ScreenFocusBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navControl: NavHostController) {

    var dialogNewActivity by remember { mutableStateOf(false) }
    var editDailyChecklist by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    val newGroup =
        TrackerActivityGroup(0, stringResource(id = R.string.screen_activities_new_group), 0)

    val vm = hiltViewModel<ActivitiesViewModel>()

    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = { 3 }
    )

    val dailyChecklistItemViewModel = hiltViewModel<DailyChecklistViewModel>()


    if (editDailyChecklist) {
        EditDailyChecklistItemBottomSheet(
            onDismissRequest = {
                editDailyChecklist = false
            },
            isEdit = false,
            item = DailyChecklistItem(
                title = "",
                color = Colors.chooseableColors[0].toArgb(),
                description = ""
            ),
            onItemEdit = {
                dailyChecklistItemViewModel.onAdd(item = it)
            }
        )
    }

    val focusBoardViewModel = hiltViewModel<FocusBoardViewModel>()
    val tags by focusBoardViewModel.tags.collectAsStateWithLifecycle()

    var editFocusItem by remember { mutableStateOf(false) }

    if (editFocusItem){
        BottomSheetEditFocusItem(
            onDismissRequest = {
                editFocusItem = false
            },
            isEdit = false,
            item = FocusBoardItemWithTags(FocusBoardItem(title = "", content = ""), listOf()),
            tags = tags,
            onFocusItemEdit = {
                focusBoardViewModel.createNewFocusItem(it)
            }
        )
    }

    if (dialogNewActivity) {
        AddActivityBottomSheet(
            state = rememberAppBottomSheetState(),
            onDismissRequest = {
                dialogNewActivity = false
            },
            onAddFolder = {
                dialogNewActivity = false

                scope.launch {
                    if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID) {
                        pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                        delay(100)
                    }
                    vm.addGroup(newGroup)
                }
            },
            onAddActivity = {
                dialogNewActivity = false

                scope.launch(Dispatchers.Main) {
                    if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID) {
                        pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                        delay(100)
                    }

                    val activityId = withContext(Dispatchers.IO) {
                        vm.createNewActivity(name, it).await()
                    }

                    navControl.navigate(Destinations.ScreenActivity(activityId))
                }
            },

            onAddFocusItem = {
                dialogNewActivity = false

                scope.launch {
                    if (pagerState.currentPage != SCREEN_FOCUS_BOARD_PAGER_ID) {
                        pagerState.animateScrollToPage(SCREEN_FOCUS_BOARD_PAGER_ID)
                        delay(100)
                    }

                    editFocusItem = true
                }
            },

            onAddDailyChecklist = {
                dialogNewActivity = false

                scope.launch {
                    if (pagerState.currentPage != SCREEN_MIND_BOOT_ID) {
                        pagerState.animateScrollToPage(SCREEN_MIND_BOOT_ID)
                        delay(100)
                    }

                    editDailyChecklist = true
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogNewActivity = true },
                modifier = Modifier.testTag(TestTag.DASHBOARD_ADD_ACTIVITY)
            ) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = {
            HorizontalPager(
                contentPadding = it,
                state = pagerState,
            ) { page ->
                when (page) {
                    SCREEN_ACTIVITIES_PAGER_ID -> ScreenActivities(navController = navControl)
                    SCREEN_FOCUS_BOARD_PAGER_ID -> ScreenFocusBoard()
                    SCREEN_MIND_BOOT_ID -> ScreenMindBoot()
                }
            }
        },
        containerColor = Colors.AppBackground
    )
}
