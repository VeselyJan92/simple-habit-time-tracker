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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.day_history.ScreenDayRecords
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.timeline.ScreenTimeline
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter


import com.imfibit.activitytracker.core.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first


fun SCREEN_ACTIVITY(activity: String = "{activity_id}") = "screen_activity/$activity"


const val SCREEN_ACTIVITIES = "screen_activities"
const val SCREEN_STATISTICS = "SCREEN_STATISTICS"
const val SCREEN_UPCOMING = "SCREEN_UPCOMING"
const val SCREEN_TIMELINE = "SCREEN_TIMELINE"
const val SCREEN_TIMER_OVER = "SCREEN_TIMER_OVER"
const val SCREEN_ONBOARDING= "SCREEN_ONBOARDING"

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


sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Statistics : Screen(SCREEN_STATISTICS, R.string.screen_title_statistics, Icons.Filled.InsertChart)
    object Activities : Screen(SCREEN_ACTIVITIES, R.string.screen_title_activities, Icons.Filled.AssignmentTurnedIn)
    object Upcoming   : Screen(SCREEN_TIMELINE, R.string.screen_title_timeline, Icons.Filled.Timeline)
}

@Composable
fun AppBottomNavigation(navController: NavController) {
    BottomNavigation {

        val currentRoute = navController.currentBackStackEntry?.destination?.route

        listOf(Screen.Statistics, Screen.Activities, Screen.Upcoming).forEach { screen ->
            BottomNavigationItem(
                    icon = { Icon(screen.icon, "todo") },

                    label = { Text(stringResource(id = screen.resourceId)) },
                    selected = currentRoute == screen.route,
                    alwaysShowLabel = true,
                    onClick = {
                        // This if check gives us a "singleTop" behavior where we do not create a
                        // second instance of the composable if we are already on that destination
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route)
                        }
                    }
            )
        }
    }
}

@Composable
fun Router(){
    val navControl = rememberNavController()

    val context: Context = LocalContext.current

    val onboarded =  runBlocking {
       context.dataStore.data.first()[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val destination = if (onboarded) SCREEN_ACTIVITIES else SCREEN_ONBOARDING


    NavHost(navController = navControl, startDestination = destination){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl) }
        composable(SCREEN_TIMELINE){ ScreenTimeline(navControl) }
        composable(SCREEN_ACTIVITIES){ ScreenActivities(navControl) }
        composable(SCREEN_UPCOMING){ ScreenUpcoming(navControl) }
        composable(SCREEN_ONBOARDING){ ScreenOnboarding(navControl) }


        composable(
                route ="screen_activity/{activity_id}",
                arguments = listOf(navArgument("activity_id") { type = NavType.LongType })
        ){
            ScreenTrackedActivity(navControl)
        }

        composable(
                route = "screen_day_history/{activity_id}/{date}",
                arguments = listOf(
                        navArgument("activity_id") { type = NavType.LongType },
                        navArgument("date") { type = NavType.StringType }
                )
        ){
            ScreenDayRecords()
        }
    }
}
