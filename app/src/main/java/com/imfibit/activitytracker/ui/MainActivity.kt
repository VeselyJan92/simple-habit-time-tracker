package com.imfibit.activitytracker.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.Destinations.ScreenActivityGroupRoute
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import com.imfibit.activitytracker.ui.components.dialogs.DialogRecords
import com.imfibit.activitytracker.ui.navigation.EditRecord
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


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

        setContent {
            Router()
        }
    }

}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

@Composable
fun MainActivity.Router() {
    val vm = hiltViewModel<AppViewModel>()

    val navControl = rememberNavController()

    val onboarded = runBlocking { vm.settings.getOnboarded() ?: false }

    val destination: Any = if (onboarded) Destinations.ScreenActivities else Destinations.ScreenOnboarding

    val scaffoldState = rememberScaffoldState()

    CompositionLocalProvider(LocalNavController provides rememberNavController()) {

        NavHost(navController = navControl, startDestination = destination) {
            composable<Destinations.ScreenStatistics> {
                ScreenStatistics(navControl, scaffoldState)
            }

            composable<Destinations.ScreenActivities> {
                Dashboard(navControl, scaffoldState)
            }

            composable<Destinations.ScreenSettings> {
                ScreenSetting(navControl, scaffoldState)
            }

            composable<Destinations.ScreenOnboarding>{
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
                ScreenActivityGroup(navControl, scaffoldState)
            }

            composable<Destinations.ScreenActivity>{
                ScreenTrackedActivity(navControl, scaffoldState)
            }

            composable<Destinations.ScreenActivityHistory> {
                ScreenActivityHistory(navControl, scaffoldState)
            }

            //TODO refactor later when custom serialzier are implemented in navigation 2.9
            dialog(
                route = "screen_day_history/{activity_id}/{date}",
                arguments = listOf(
                    navArgument("activity_id") { type = NavType.LongType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) {
                DialogRecords(navControl, scaffoldState)
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

@Composable
private fun Dashboard(navControl: NavHostController, scaffoldState: ScaffoldState) {

    val dialogNewActivity = remember { mutableStateOf(false) }
    val dialogEditFocusItem = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val name = stringResource(id = R.string.new_activity_name)

    val newGroup =
        TrackerActivityGroup(0, stringResource(id = R.string.screen_activities_new_group), 0)

    val vm = hiltViewModel<ActivitiesViewModel>()
    val focusBoardViewModel = hiltViewModel<FocusBoardViewModel>()

    val tags by focusBoardViewModel.tags.collectAsStateWithLifecycle()


    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )

    DialogEditFocusItem(
        display = dialogEditFocusItem,
        isEdit = false,
        item = FocusBoardItemWithTags(FocusBoardItem(title = "", content = ""), listOf()),
        tags = tags,
        onFocusItemEdit = {
            focusBoardViewModel.createNewFocusItem(it)
        }
    )

    DialogAddActivity(display = dialogNewActivity,
        onAddFolder = {
            dialogNewActivity.value = false

            scope.launch {
                if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID) {
                    pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                    delay(100)
                }

                vm.addGroup(newGroup)
            }
        },
        onAddActivity = {
            dialogNewActivity.value = false

            scope.launch(Dispatchers.Main) {
                if (pagerState.currentPage != SCREEN_ACTIVITIES_PAGER_ID) {
                    pagerState.animateScrollToPage(SCREEN_ACTIVITIES_PAGER_ID)
                    delay(100)
                }

                val activityId = withContext(Dispatchers.IO) {
                    vm.createNewActivity(name, it).await()
                }

                navControl.navigate(Destinations.ScreenActivity(activityId))
            }
        },

        onAddFocusItem = {
            dialogNewActivity.value = false

            scope.launch {
                if (pagerState.currentPage != SCREEN_FOCUS_BOARD_PAGER_ID) {
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
            FloatingActionButton(
                onClick = { dialogNewActivity.value = true },
                modifier = Modifier.testTag(TestTag.DASHBOARD_ADD_ACTIVITY)
            ) {
                Icon(Icons.Filled.Add, null)
            }
        },
        content = {
            HorizontalPager(
                contentPadding = it,
                state = pagerState,
            ) { page ->
                when (page) {
                    SCREEN_ACTIVITIES_PAGER_ID -> ScreenActivities(navController = navControl)
                    SCREEN_FOCUS_BOARD_PAGER_ID -> ScreenFocusBoard()
                }
            }
        },
        backgroundColor = Colors.AppBackground
    )
}


@Composable
fun MainBody(content: @Composable (ColumnScope.() -> Unit)) {
    Column(modifier = Modifier.fillMaxSize(), content = content)
}


