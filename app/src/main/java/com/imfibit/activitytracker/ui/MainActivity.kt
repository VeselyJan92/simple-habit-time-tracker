package com.imfibit.activitytracker.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.work.*
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.day_history.ScreenDayRecords
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.timeline.ScreenTimeline
import com.imfibit.activitytracker.ui.screens.timer_over.ScreenTimerOver
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import com.imfibit.activitytracker.work.ScheduledTimer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri


fun SCREEN_ACTIVITY(activity: String = "{activity_id}") = "screen_activity/$activity"



const val SCREEN_ACTIVITIES = "screen_activities"
const val SCREEN_STATISTICS = "SCREEN_STATISTICS"
const val SCREEN_UPCOMING = "SCREEN_UPCOMING"
const val SCREEN_DAY_HISTORY = "SCREEN_DAY_HISTORY"

const val SCREEN_TIMER_OVER = "SCREEN_TIMER_OVER"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val name = "timer"
        val descriptionText = "DESC"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("xx", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)


        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        channel.setSound(soundUri, audioAttributes)


        notificationManager.createNotificationChannel(channel)


        val x = OneTimeWorkRequestBuilder<ScheduledTimer>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()


        WorkManager.getInstance(this).enqueue(x)







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

    NavHost(navController = navControl, startDestination = SCREEN_TIMER_OVER){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl) }
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl) }
        composable(SCREEN_ACTIVITIES){ ScreenActivities(navControl) }
        composable(SCREEN_UPCOMING){ ScreenUpcoming(navControl) }

        composable(SCREEN_TIMER_OVER){ ScreenTimerOver(navControl) }


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
