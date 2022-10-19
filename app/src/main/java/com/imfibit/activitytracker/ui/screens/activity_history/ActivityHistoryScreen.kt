package com.imfibit.activitytracker.ui.screens.activity_history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Month
import com.imfibit.activitytracker.ui.components.SimpleTopBar
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ScreenActivityHistory(nav: NavHostController, scaffoldState: ScaffoldState) {

    val vm = hiltViewModel<TrackedActivityHistoryVM>()

    val activity by vm.activity.collectAsState(initial = null)

    val months = vm.months

    Scaffold(
        topBar = {
            SimpleTopBar(nav, stringResource(id = R.string.screen_title_record_history))
        },
        content = {
            ScreenBody(nav, activity, months)
        },
        backgroundColor = Colors.AppBackground,
        scaffoldState = scaffoldState
    )
}

@Composable
private fun ScreenBody(
    nav: NavHostController,
    activity: TrackedActivity?,
    months: Flow<PagingData<RepositoryTrackedActivity.Month>>
){
    Column(Modifier) {

        val x = months.collectAsLazyPagingItems()

        if (activity != null){

                LazyColumn(reverseLayout = true){
                    itemsIndexed(x){ index,  item ->

                        if (item != null){
                            Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp), shape = RoundedCornerShape(20.dp)) {
                                Column(Modifier.padding(8.dp)) {
                                    Month(activity = activity, month = item, nav = nav)
                                }
                            }
                        }

                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

}
