package com.imfibit.activitytracker.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.core.navigation.BottomSheetSceneStrategy
import com.imfibit.activitytracker.ui.Destinations.ScreenActivityGroupRoute
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.navigation.EditRecord
import com.imfibit.activitytracker.ui.screens.dashboard.DashboardScreen
import com.imfibit.activitytracker.ui.screens.focus_board.ScreenEditFocusBoardItem
import com.imfibit.activitytracker.ui.screens.focus_board.ScreenFocusBundle
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.tracked_activities.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.tracked_activities.statistics.ScreenStatistics
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavHost() {
    val vm = hiltViewModel<AppViewModel>()

    val navigation = hiltViewModel<BackstackViewModel>()

    val backStack by navigation.backStack.collectAsState()

    val entryProvider = remember(backStack) {
        entryProvider {
            entry<Destinations.ScreenStatistics> {
                ScreenStatistics()
            }
            entry<Destinations.ScreenActivities> {
                DashboardScreen()
            }
            entry<Destinations.ScreenSettings> {
                ScreenSetting()
            }
            entry<Destinations.ScreenOnboarding> {
                ScreenOnboarding(
                    onOnboardingDone = {
                        runBlocking {
                            vm.settings.setOnboarded(true)
                        }
                        navigation.navigate(Destinations.ScreenActivities)
                    }
                )
            }
            entry<ScreenActivityGroupRoute> { destination ->
                ScreenActivityGroup(
                    groupId = destination.groupId
                )
            }
            entry<Destinations.ScreenActivity> { destination ->
                ScreenTrackedActivity(
                    activityId = destination.activityId
                )
            }
            entry<Destinations.ScreenFocusBundleDetail> { destination ->
                ScreenFocusBundle(bundleId = destination.bundleId)
            }
            entry<Destinations.ScreemEditFocusBoardItem> { destination ->
                ScreenEditFocusBoardItem(
                    bundleId = destination.bundleId,
                    noteId = destination.noteId,
                )
            }
            entry<Destinations.DialogActivityDayHistory>(
                metadata = DialogSceneStrategy.dialog()
            ) { destination ->
                DialogRecords(
                    activityId = destination.activityId,
                    date = destination.getDate()
                )
            }
            entry<Destinations.DialogEditRecord>(
                metadata = DialogSceneStrategy.dialog()
            ) { destination ->
                EditRecord(
                    record = destination.item
                )
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { navigation.popBackStack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        predictivePopTransitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        popTransitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },

        sceneStrategies = listOf(
            remember { DialogSceneStrategy() },
            remember { BottomSheetSceneStrategy() },
        ),
    )
}
