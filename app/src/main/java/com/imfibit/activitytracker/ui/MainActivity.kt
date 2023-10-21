package com.imfibit.activitytracker.ui


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.screens.activity.ScreenTrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_history.ScreenActivityHistory
import com.imfibit.activitytracker.ui.screens.activity_list.ActivitiesViewModel
import com.imfibit.activitytracker.ui.screens.activity_list.ScreenActivities
import com.imfibit.activitytracker.ui.screens.focus_board.DialogEditFocusItem
import com.imfibit.activitytracker.ui.screens.focus_board.FocusBoardViewModel
import com.imfibit.activitytracker.ui.screens.focus_board.ScreenFocusBoard
import com.imfibit.activitytracker.ui.screens.group.ScreenActivityGroup
import com.imfibit.activitytracker.ui.screens.onboarding.ScreenOnboarding
import com.imfibit.activitytracker.ui.screens.settings.ScreenSetting
import com.imfibit.activitytracker.ui.screens.statistics.ScreenStatistics
import com.imfibit.activitytracker.ui.screens.upcomming.ScreenUpcoming
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


fun SCREEN_ACTIVITY(activity: String = "{activity_id}") = "screen_activity/$activity"


const val SCREEN_ACTIVITIES = "screen_activities"
const val SCREEN_STATISTICS = "SCREEN_STATISTICS"
const val SCREEN_UPCOMING = "SCREEN_UPCOMING"
const val SCREEN_TIMELINE = "SCREEN_TIMELINE"
const val SCREEN_TIMER_OVER = "SCREEN_TIMER_OVER"
const val SCREEN_ONBOARDING= "SCREEN_ONBOARDING"
const val SCREEN_SETTINGS= "SCREEN_SETTINGS"



const val SCREEN_FOCUS_BOARD_PAGER_ID = 0
const val SCREEN_ACTIVITIES_PAGER_ID = 1

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

        val barColor = Colors.AppBackground.toArgb()
        window.statusBarColor = barColor
        window.navigationBarColor = barColor

        setContent {
            Router()
        }
    }

}



@Composable
fun MainActivity.Router(){
    val navControl = rememberNavController()

    val context: Context = LocalContext.current

    val onboarded =  runBlocking {
       context.dataStore.data.first()[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val destination = if (onboarded) SCREEN_ACTIVITIES else SCREEN_ONBOARDING

    val scaffoldState = rememberScaffoldState()

    NavHost(navController = navControl, startDestination = destination){
        composable(SCREEN_STATISTICS){ ScreenStatistics(navControl, scaffoldState) }
        composable(SCREEN_ACTIVITIES){ Dashboard(navControl, scaffoldState) }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Dashboard(navControl: NavHostController, scaffoldState: ScaffoldState) {

    val dialogNewActivity = remember { mutableStateOf(false) }
    val dialogEditFocusItem = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    val newGroup = TrackerActivityGroup(0, stringResource(id = R.string.screen_activities_new_group), 0)

    val vm = hiltViewModel<ActivitiesViewModel>()
    val focusBoardViewModel = hiltViewModel<FocusBoardViewModel>()


    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = {2}
    )

    DialogEditFocusItem(
        display = dialogEditFocusItem,
        isEdit = false,
        item = FocusBoardItemWithTags(FocusBoardItem(title = "", content = ""), listOf()),
        tags = focusBoardViewModel.tags,
        onFocusItemEdit = {
            focusBoardViewModel.createNewFocusItem(it)
        }
    )

    DialogAddActivity(display = dialogNewActivity,
        onAddFolder = {
            dialogNewActivity.value = false

            scope.launch {
                if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID){
                    pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                    delay(100)
                }

                vm.addGroup(newGroup)
            }
        },
        onAddActivity = {
            dialogNewActivity.value = false

            scope.launch {
                if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID){
                    pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                    delay(100)
                }

                val activityId = vm.createNewActivity(name, it).await()

                navControl.navigate("screen_activity/$activityId")
            }
        },

        onAddFocusItem = {
            dialogNewActivity.value = false

            scope.launch {
                if (pagerState.currentPage != SCREEN_FOCUS_BOARD_PAGER_ID){
                    pagerState.animateScrollToPage(SCREEN_FOCUS_BOARD_PAGER_ID)
                    delay(100)
                }

                dialogEditFocusItem.value = true
            }
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { dialogNewActivity.value = true }) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = {
            HorizontalPager(
                contentPadding = it,
                state = pagerState,
            ) { page ->
                when(page){
                    SCREEN_ACTIVITIES_PAGER_ID -> ScreenActivities(navController = navControl )
                    SCREEN_FOCUS_BOARD_PAGER_ID -> ScreenFocusBoard(navControl = navControl)
                }
            }
        },
        backgroundColor = Colors.AppBackground
    )
}


@Composable
fun MainBody(content: @Composable (ColumnScope.() -> Unit)) {
    Column (modifier = Modifier.fillMaxSize(), content = content)
}


