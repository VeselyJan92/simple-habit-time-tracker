package com.janvesely.activitytracker.ui.screens.activity_list

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.core.App
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivity.Type
import com.janvesely.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.janvesely.activitytracker.ui.components.BaseMetricData
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.MetricBlock
import com.janvesely.activitytracker.ui.components.dialogs.DialogSession
import com.janvesely.activitytracker.ui.components.dialogs.DialogScore
import com.janvesely.getitdone.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime


data class TrackedActivityWithMetric constructor(
    var activity: TrackedActivity,
    val past: List<BaseMetricData>,
) {
    val completed = activity.expected <= past[0].metric
}


@OptIn(ExperimentalFocus::class, ExperimentalFoundationApi::class)
@Composable
fun TrackedActivitiesList(
    nav: NavController,
    vm: ActivitiesViewModel
){
    val items by vm.activities.observeAsState(arrayListOf())

    LazyColumnForIndexed(
        items = items,
        Modifier.padding(8.dp)
    ) { index, item ->

        val requestEdit = remember { mutableStateOf(false) }


        if (requestEdit.value) when(item.activity.type){
            Type.SESSION -> DialogSession(
                display = requestEdit,
                activityId = item.activity.id,
                from = LocalDateTime.now(),
                to = LocalDateTime.now(),
            )
            Type.SCORE -> DialogScore(
                display = requestEdit,
                activityId = item.activity.id,
                datetime = LocalDateTime.now(),
                score = 1
            )
        }


        if (index != 0){
            Spacer(modifier = Modifier.height(8.dp))
        }

        Surface(
            modifier = Modifier
                .clickable(
                    onClick = {
                        nav.navigate(R.id.action_navigation_dashboard_to_activity_fragment, bundleOf(
                            "tracked_activity_id" to item.activity.id
                        ))
                    }
                ),
            elevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White).padding(top = 8.dp, bottom = 8.dp, end = 8.dp)

            ) {
                Stack(
                    alignment = Alignment.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(end = 8.dp, start = 8.dp)
                        .clickable(
                            onClick = {
                                GlobalScope.launch {
                                    when(item.activity.type){
                                        Type.SESSION ->  vm.rep.startSession(item.activity.id)
                                        Type.SCORE ->  vm.rep.commitScore(item.activity.id, LocalDateTime.now(), 1)
                                        Type.COMPLETED -> {
                                            val record = vm.rep.completionDAO.getRecord(item.activity.id, LocalDate.now())

                                            if (record != null)
                                                vm.rep.completionDAO.delete(record)
                                            else
                                                vm.rep.commitCompletion(item.activity.id, LocalDate.now())
                                        }
                                    }
                                }
                            },
                            onLongClick = {
                                val viber =  App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                viber.vibrate(VibrationEffect.createOneShot(50L, 1))
                                RepositoryTrackedActivity()

                                if (item.activity.type!= Type.COMPLETED)
                                    requestEdit.value = true
                            }
                        )
                ) {
                    Icon(
                        when(item.activity.type){
                            Type.SESSION -> Icons.Filled.PlayArrow
                            Type.SCORE -> Icons.Filled.Add
                            Type.COMPLETED -> if (item.completed) Icons.Filled.DoneAll else Icons.Filled.Check
                        },
                        Modifier
                            .size(34.dp)
                            .background(Colors.AppAccent, RoundedCornerShape(17.dp))
                    )
                }

                Column(Modifier.fillMaxWidth()){
                    Row {
                        Text(
                            item.activity.name,
                            Modifier.weight(1f),
                            style = TextStyle(
                                fontWeight = FontWeight.W600,
                                fontSize = 18.sp
                            )
                        )

                        if (item.activity.isGoalSet())
                            Goal(item.activity.type.format(item.activity.expected))
                    }

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricBlock(item.past[0], editable = false, width = 80.dp, metricStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold) )
                        MetricBlock(item.past[1], editable = true)
                        MetricBlock(item.past[2], editable = true)
                        MetricBlock(item.past[3], editable = true)
                        MetricBlock(item.past[4], editable = true)
                    }
                }
            }
        }

    }

}

@Composable
private fun Goal(label: String){
    Row(Modifier.size(70.dp, 20.dp).padding(end = 8.dp).background(Colors.ChipGray, RoundedCornerShape(50))) {
        Icon(Icons.Filled.Flag, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            label,
            Modifier.weight(1f).align(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )

        )
    }

}

/*
@Composable
private fun CurrentMetric(data: MetricBlockData){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = data.getLabel(), style = TextStyle(
            fontSize = 10.sp
        ))

        val color = if (data.isCompleted()) Colors.ButtonGreen else Colors.NotCompleted

        val modifier = Modifier.size(80.dp, 20.dp).background(color, RoundedCornerShape(10.dp))
        
        Surface(
            elevation = 2.dp,
            shape =  RoundedCornerShape(10.dp)
        ) {
            Stack(modifier = modifier){
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = data.formatMetric(),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp
                    )
                )
            }
        }

    }

}*/



