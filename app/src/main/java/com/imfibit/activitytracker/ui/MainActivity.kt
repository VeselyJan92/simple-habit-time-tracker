package com.imfibit.activitytracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.day_history.ScreenDayRecords
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.timeline.ScreenTimeline
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun SCREEN_ACTIVITY(activity: String = "{activity_id}") = "screen_activity/$activity"



const val SCREEN_ACTIVITIES = "screen_activities"
const val SCREEN_STATISTICS = "SCREEN_STATISTICS"
const val SCREEN_UPCOMING = "SCREEN_UPCOMING"
const val SCREEN_DAY_HISTORY = "SCREEN_DAY_HISTORY"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            Router(navController)
        }
    }
}


sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Statistics : Screen(SCREEN_STATISTICS, R.string.screen_title_statistics, Icons.Filled.InsertChart)
    object Activities : Screen(SCREEN_ACTIVITIES, R.string.screen_title_activities, Icons.Filled.AssignmentTurnedIn)
    object Upcoming   : Screen("timeline", R.string.screen_title_timeline, Icons.Filled.Timeline)
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
fun Router(navControl: NavHostController){

    NavHost(navController = navControl, startDestination = SCREEN_ACTIVITIES){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl) }
        composable(SCREEN_ACTIVITIES){ ScreenActivities(navControl) }
        composable(SCREEN_UPCOMING){ ScreenUpcoming(navControl) }
        composable("timeline"){ ScreenTimeline(navControl) }


        composable(
                route ="screen_activity/{activity_id}",
                arguments = listOf(navArgument("activity_id") { type = NavType.LongType })
        ){
            ScreenTrackedActivity(navControl, it.arguments?.getLong("activity_id")!!)
        }


        composable(
                route = "screen_day_history/{activity_id}/{date}",
                arguments = listOf(
                        navArgument("activity_id") { type = NavType.LongType },
                        navArgument("date") { type = NavType.StringType }
                )
        ){
            ScreenDayRecords(
                    navControl,
                    it.arguments?.getLong("activity_id")!!,
                    LocalDate.parse(it.arguments?.getString("date"), DateTimeFormatter.ISO_DATE)
            )
        }
    }
}
