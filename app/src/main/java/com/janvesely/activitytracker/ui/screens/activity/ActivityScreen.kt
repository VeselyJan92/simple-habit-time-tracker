package com.janvesely.activitytracker.ui.screens.activity

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.database.composed.ViewRangeData
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.Colors
import java.time.LocalDateTime

@Preview
@Composable
fun ActivityScreen(){

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        val data = ViewRangeData(TimeRange.DAILY, LocalDateTime.now(), LocalDateTime.now()).apply {
            this.format = TrackedActivity.Type.SESSION
        }

        MetricBlock("Today", "00:00")
        MetricBlock("Week", "00:00")
        MetricBlock("Month", "00:00")
        MetricBlock("30 days", "00:00")

    }
}



@Composable
fun MetricBlock(label: String, metric: String){
    Column(Modifier.padding(8.dp).size(60.dp).background(Color.White), horizontalGravity = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = label, style = TextStyle(
            fontSize = 10.sp
        ))



        Stack(modifier =   Modifier.size(40.dp, 20.dp).background(Colors.AppAccent, RoundedCornerShape(10.dp))){

            Text(
                modifier = Modifier.gravity(Alignment.Center),
                text = metric,
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp
                )
            )
        }

    }

}
