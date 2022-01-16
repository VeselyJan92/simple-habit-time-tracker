package com.imfibit.activitytracker.ui.screens.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue


import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Record
import com.imfibit.activitytracker.ui.components.TrackedActivityRecord
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
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
        content = {
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
    val vm = hiltViewModel<TimelineVM>()

    val items by vm.records.observeAsState(listOf())
    
    if (items.isEmpty()){
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(), contentAlignment = Alignment.Center) {
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
        Column {
            Header()

            LazyColumn(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(items.size) {
                    Record(items[it].activity, items[it].record)
                }
            }
        }
    }
    
}


@Composable
private fun Header(){
    Surface(
        elevation = 2.dp,
        modifier = Modifier.padding(8.dp).fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = stringResource(id = R.string.today), style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.W600
            ))
        }
    }
}




