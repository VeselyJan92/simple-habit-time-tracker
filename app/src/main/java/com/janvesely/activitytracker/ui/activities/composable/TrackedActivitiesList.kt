package com.janvesely.activitytracker.ui.activities.composable

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ExperimentalLayoutNodeApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.database.Seeder
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.activitytracker.database.composed.ViewRangeData
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.Colors


@OptIn(ExperimentalLayoutNodeApi::class)
@ExperimentalLayout
@Preview
@Composable
fun Temp(){
    val data = listOf(
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Programování",
            type = TrackedActivity.Type.SESSION,
            goal = 60 * 60 * 2
        )),
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Posilovna",
            type = TrackedActivity.Type.COMPLETED,

        )),
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Shyby",
            type = TrackedActivity.Type.SCORE,

        )),
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Programování",
            type = TrackedActivity.Type.SESSION,
            goal = 60 * 60 * 5
        )),
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Posilovna",
            type = TrackedActivity.Type.COMPLETED,
        )),
        Seeder().getTrackedActivityWithMetric(activity = Seeder().getTrackedActivity(
            name = "Shyby",
            type = TrackedActivity.Type.SCORE,
            goal = 3
        )),

    )

    TrackedActivitiesList(MutableLiveData(data))
}


@ExperimentalLayoutNodeApi
@Composable
fun TrackedActivitiesList(data: LiveData<List<TrackedActivityWithMetric>>){
    val items = data.observeAsState(listOf())

    var first = true


    LazyColumnForIndexed(
        items = data.value!!,
        Modifier.padding(8.dp)
    ) { index, item ->

        if (index != 0){
            Spacer(modifier = Modifier.height(8.dp))
        }

        Surface(elevation = 2.dp) {
            Row(
                modifier = Modifier
                    .background(Color.White).padding(top = 8.dp, bottom = 8.dp, end = 8.dp)


            ) {

                IconButton(onClick ={}, Modifier.gravity(Alignment.CenterVertically).padding(end = 8.dp, start = 8.dp) ) {
                    Icon(
                        when(item.activity.type){
                            TrackedActivity.Type.SESSION -> Icons.Filled.PlayArrow
                            TrackedActivity.Type.SCORE -> Icons.Filled.Add
                            TrackedActivity.Type.COMPLETED -> Icons.Filled.Check
                        },
                        Modifier
                            .size(34.dp)
                            .background(Colors.ChipGray, RoundedCornerShape(17.dp))
                    )


                }

                Column(Modifier.fillMaxWidth()){

                    Row() {
                        Text(
                            item.activity.name,
                            Modifier.weight(1f),
                            style = TextStyle(
                                fontWeight = FontWeight.W600,
                                fontSize = 18.sp
                            )
                        )


                        if (item.activity.isGoalSet())

                            ProgressIndicator(item.past.last())

                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalGravity = Alignment.CenterVertically) {
                        repeat(5){
                            MetricBlock(item.past[4-it], it)
                        }
                    }

                }
            }
        }

    }

}

@Composable
fun MetricBlock(data: ViewRangeData, position: Int){
    Column(horizontalGravity = Alignment.CenterHorizontally) {
        Text(text = data.getLabel(), style = TextStyle(
            fontSize = 10.sp
        ))

        val color = if (data.isCompleted()) Colors.Completed else Colors.NotCompleted



        val modifier = if (position == 0)
            Modifier.size(80.dp, 20.dp).background(color, RoundedCornerShape(10.dp))
        else
            Modifier.size(40.dp, 20.dp).background(color, RoundedCornerShape(10.dp))

        Surface(
            elevation = if (position == 0) 2.dp else 0.dp,
            shape =  RoundedCornerShape(10.dp)
        ) {
            Stack(modifier = modifier){

                Text(
                    modifier = Modifier.gravity(Alignment.Center),
                    text = data.formatMetric(),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = if (position == 0) 15.sp else 10.sp
                    )
                )
            }
        }


    }

}

@Composable
fun ProgressIndicator(recent: ViewRangeData){
    Row(Modifier.size(80.dp, 20.dp).padding(start = 16.dp, end = 0.dp).background(Colors.ChipGray, RoundedCornerShape(50))) {
        Icon(Icons.Filled.Flag, Modifier.gravity(Alignment.CenterVertically).padding(start = 5.dp).size(20.dp))

        Text(
            recent.formatGoal(),
            Modifier.weight(1f).gravity(Alignment.CenterVertically).padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )

        )
    }

}
