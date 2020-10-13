package com.janvesely.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.janvesely.activitytracker.core.TimeUtils
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.Colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime




@Composable
fun LiveActivitiesList(vm: ActivitiesViewModel){

    val items: List<TrackedActivity> by vm.live.observeAsState(listOf())

    val (time, setTime) = remember { mutableStateOf(LocalDateTime.now()) }

    launchInComposition{

        while (this.coroutineContext.isActive){
            setTime(LocalDateTime.now())
            delay(1000)
        }
    }

    LazyColumnForIndexed(
        modifier = Modifier.clip(RoundedCornerShape(topLeft = 20.dp, topRight = 20.dp)).background(Color.White),
        items = items
    ) { index: Int, item: TrackedActivity ->

        if (index != 0){
            Divider(color = Colors.ChipGray, thickness = 1.dp)
        }

        Row(Modifier.fillParentMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {


            IconButton(onClick = { vm.stopSession(item)}) {
                Box(Modifier.size(20.dp, 20.dp).background(Color.Red))
            }

            Text(text = item.name, Modifier.weight(1f), style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ))

            Row(
                modifier = Modifier
                    .size(130.dp, 25.dp)
                    .padding(end = 8.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))

            ) {
                Icon(
                    Icons.Filled.Timer,
                    Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(20.dp)
                )


                Text(TimeUtils.secondsToMetric(item.inSessionSince!!, time),
                    Modifier.weight(1f).align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                )


            }


        }
    }

}
@Composable
fun StaticActivity(){

}