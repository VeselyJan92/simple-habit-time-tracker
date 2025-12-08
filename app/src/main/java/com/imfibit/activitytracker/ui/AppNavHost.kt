package com.imfibit.activitytracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.ui.Destinations.ScreenActivityGroupRoute
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.navigation.EditRecord
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.dashboard.Dashboard
import com.imfibit.activitytracker.ui.screens.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavHost() {
    val vm = hiltViewModel<AppViewModel>()
    val onboarded = runBlocking { vm.settings.getOnboarded() ?: false }

    val startDestination: AppDestination =
        if (onboarded) Destinations.ScreenActivities else Destinations.ScreenOnboarding

    val backStack = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableListOf<AppDestination>(startDestination).toMutableStateList()
    }

    val entryProvider = remember(backStack) {
        entryProvider {
            entry<Destinations.ScreenStatistics> {
                ScreenStatistics(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                )
            }
            entry<Destinations.ScreenActivities> {
                Dashboard(
                    navigate = { backStack.add(it) }
                )
            }
            entry<Destinations.ScreenSettings> {
                ScreenSetting(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                )
            }
            entry<Destinations.ScreenOnboarding> {
                ScreenOnboarding(
                    onOnboardingDone = {
                        runBlocking {
                            vm.settings.setOnboarded(true)
                        }
                        backStack.add(Destinations.ScreenActivities)
                    }
                )
            }
            entry<ScreenActivityGroupRoute> { destination ->
                ScreenActivityGroup(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    groupId = destination.groupId
                )
            }
            entry<Destinations.ScreenActivity> { destination ->
                ScreenTrackedActivity(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    activityId = destination.activityId
                )
            }
            entry<Destinations.DialogActivityDayHistory>(
                metadata = DialogSceneStrategy.dialog()
            ) { destination ->
                DialogRecords(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    activityId = destination.activityId,
                    date = destination.getDate()
                )
            }
            entry<Destinations.DialogEditRecord>(
                metadata = DialogSceneStrategy.dialog()
            ) { destination ->
                EditRecord(
                    navigate = { backStack.add(it) },
                    popBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    record = destination.item
                )
            }
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    NavDisplay(
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSaveableStateHolderNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider,
        sceneStrategy = remember { DialogSceneStrategy() }
    )
}
