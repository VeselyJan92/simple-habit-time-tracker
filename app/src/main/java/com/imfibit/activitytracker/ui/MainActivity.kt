package com.imfibit.activitytracker.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.MainActivity.Companion.NOTIFICATION_NAVIGATE_TO_ACTIVITY
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_history.ScreenActivityHistory
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


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

    @Inject
    lateinit var db: AppDatabase

    companion object {
        const val NOTIFICATION_NAVIGATE_TO_ACTIVITY = "NAV_TO_ACTIVITY"
    }

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
fun MainActivity.Router(){

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


