package com.imfibit.activitytracker.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.ui.Destinations.ScreenActivityGroupRoute
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.navigation.EditRecord
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_history.ScreenActivityHistory
import com.imfibit.activitytracker.ui.screens.dashboard.Dashboard
import com.imfibit.activitytracker.ui.screens.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavHost() {
    val vm = hiltViewModel<AppViewModel>()

    val navControl = rememberNavController()

    val onboarded = runBlocking { vm.settings.getOnboarded() ?: false }

    val destination: Any =
        if (onboarded) Destinations.ScreenActivities else Destinations.ScreenOnboarding


    CompositionLocalProvider(LocalNavController provides rememberNavController()) {

        NavHost(
            navController = navControl,
            startDestination = destination,
            popExitTransition = {
                ExitTransition.None
            },
            popEnterTransition = {
                EnterTransition.None
            }
        ) {
            composable<Destinations.ScreenStatistics> {
                ScreenStatistics(navControl)
            }

            composable<Destinations.ScreenActivities> {
                Dashboard(navControl)
            }

            composable<Destinations.ScreenSettings> {
                ScreenSetting(navControl)
            }

            composable<Destinations.ScreenOnboarding> {
                ScreenOnboarding(
                    onOnboardingDone = {
                        runBlocking {
                            vm.settings.setOnboarded(true)
                        }

                        navControl.navigate(Destinations.ScreenActivities)
                    }
                )
            }

            composable<ScreenActivityGroupRoute> {
                ScreenActivityGroup(navControl)
            }

            composable<Destinations.ScreenActivity> {
                ScreenTrackedActivity(navControl)
            }

            composable<Destinations.ScreenActivityHistory> {
                ScreenActivityHistory(navControl)
            }

            //TODO refactor later when custom serialzier are implemented in navigation 2.9
            dialog(
                route = "screen_day_history/{activity_id}/{date}",
                arguments = listOf(
                    navArgument("activity_id") { type = NavType.LongType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) {
                DialogRecords(navControl)
            }


            dialog(
                route = "dialog_edit_record/{record}",
                arguments = listOf(
                    navArgument("record") {
                        type = NavType.ParcelableType(TrackedActivityRecord::class.java)
                    },
                )
            ) {
                EditRecord(navControl)
            }
        }
    }
}
