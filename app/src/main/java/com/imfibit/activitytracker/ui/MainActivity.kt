package com.imfibit.activitytracker.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import kotlinx.coroutines.runBlocking


import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.screens.activity_history.ScreenActivityHistory
import com.imfibit.activitytracker.ui.screens.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first


fun SCREEN_ACTIVITY(activity: String = "{activity_id}") = "screen_activity/$activity"


const val SCREEN_ACTIVITIES = "screen_activities"
const val SCREEN_STATISTICS = "SCREEN_STATISTICS"
const val SCREEN_UPCOMING = "SCREEN_UPCOMING"
const val SCREEN_TIMELINE = "SCREEN_TIMELINE"
const val SCREEN_TIMER_OVER = "SCREEN_TIMER_OVER"
const val SCREEN_ONBOARDING= "SCREEN_ONBOARDING"
const val SCREEN_SETTINGS= "SCREEN_SETTINGS"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationTimerOver.createChannel(this)
        NotificationLiveSession.createChannel(this)

        setContent {
            Router()
        }
    }
}



@Composable
fun Router(){

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Colors.AppBackground,
            darkIcons = useDarkIcons
        )
    }



    val navControl = rememberNavController()

    val context: Context = LocalContext.current

    val onboarded =  runBlocking {
       context.dataStore.data.first()[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val destination = if (onboarded) SCREEN_ACTIVITIES else SCREEN_ONBOARDING


    val scaffoldState = rememberScaffoldState()

    NavHost(navController = navControl, startDestination = destination){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl, scaffoldState) }
        composable(SCREEN_ACTIVITIES){ ScreenActivities(navControl, scaffoldState) }
        composable(SCREEN_UPCOMING){ ScreenUpcoming(navControl, scaffoldState) }
        composable(SCREEN_ONBOARDING){ ScreenOnboarding(navControl, scaffoldState) }
        composable(SCREEN_SETTINGS){ ScreenSetting(navControl, scaffoldState) }

        composable(
            route ="screen_activity_group/{group_id}",
            arguments = listOf(navArgument("group_id") { type = NavType.LongType })){
            ScreenActivityGroup(navControl, scaffoldState)
        }

        composable(
                route ="screen_activity/{activity_id}",
                arguments = listOf(navArgument("activity_id") { type = NavType.LongType })
        ){
            ScreenTrackedActivity(navControl, scaffoldState)
        }

        composable(
            route ="screen_activity_history/{activity_id}",
            arguments = listOf(navArgument("activity_id") { type = NavType.LongType })
        ){
            ScreenActivityHistory(navControl, scaffoldState)
        }

        dialog(
                route = "screen_day_history/{activity_id}/{date}",
                arguments = listOf(
                        navArgument("activity_id") { type = NavType.LongType },
                        navArgument("date") { type = NavType.StringType }
                )
        ){
            DialogRecords(navControl, scaffoldState)
        }

    }
}
