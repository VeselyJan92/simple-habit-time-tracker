package com.imfibit.activitytracker.ui

import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.day_history.ScreenDayRecords
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming


const val SCREEN_ACTIVITIES = "TASK_SCREEN"
const val SCREEN_ACTIVITY = "TASK_SCREEN"
const val SCREEN_STATISTICS = "TASK_SCREEN"
const val SCREEN_UPCOMING = "TASK_SCREEN"
const val SCREEN_DAY_HISTORY = "TASK_SCREEN"


@OptIn(ExperimentalLayout::class)
@Composable
fun Router(){
    val navControl = rememberNavController()

    NavHost(navController = navControl, startDestination = SCREEN_ACTIVITIES){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl) }
        composable(SCREEN_ACTIVITIES){ ScreenActivities(navControl) }
        composable(SCREEN_ACTIVITY){ ScreenTrackedActivity(navControl) }
        composable(SCREEN_UPCOMING){ ScreenUpcoming(navControl) }
        composable(SCREEN_DAY_HISTORY){ ScreenDayRecords(navControl) }
    }
}