package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Timer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun LiveActivitiesList(vm: ActivitiesViewModel) {

    val items: List<TrackedActivity> by vm.live.observeAsState(listOf())

    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White),
    ) {
        items(items.size) { LiveActivity(vm = vm, item = items[it]) }
    }
}

@Composable
private fun LiveActivity(vm: ActivitiesViewModel, item: TrackedActivity) {



    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp), verticalAlignment = Alignment.CenterVertically) {

        val context = LocalContext.current

        IconButton(
            onClick = {
                vm.stopSession(context, item)
            }
        ) {
            Box(
                Modifier
                    .size(20.dp, 20.dp)
                    .background(Color.Red))
        }

        Column (modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(16.dp))
                

                Text(
                    text = "Cíl: 20:00",
                    modifier = Modifier.padding(start = 8.dp)
                )
                
            }

            
        }




        Timer(
            startTime = item.inSessionSince!!,
            onClick = {
                GlobalScope.launch {
                    vm.rep.activityDAO.update(item.copy(inSessionSince = it))
                }
            }
        )

    }
}




