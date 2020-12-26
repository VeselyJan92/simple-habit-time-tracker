package com.imfibit.activitytracker.ui.screens.timeline

import android.util.Log
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.invalidate


import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackedActivityRecord
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScreenTimeline(nav: NavController, scaffoldState: ScaffoldState = rememberScaffoldState()) {

    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_title_timeline))
        },
        bottomBar = {
            AppBottomNavigation(nav)
        },
        bodyContent = {
            Body(scaffoldState)
        },
        backgroundColor = Colors.AppBackground,
        snackbarHost = {
            SnackbarHost(hostState = it)
        },
        scaffoldState = scaffoldState
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Body(scaffoldState: ScaffoldState) {
    val vm = viewModel<TimelineVM>()

    val items by vm.records.observeAsState(listOf())
    
    if (items.isEmpty()){
        Box(Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.no_records),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }

    }else{
        Box {
            Box(Modifier.padding(start = 28.dp).width(20.dp). fillMaxHeight().background(Colors.AppAccent))



            LazyColumn(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(items = items) { item-> TrackedActivityRecord(item.activity, item.record, scaffoldState) }
            }

        }
    }
    
}



