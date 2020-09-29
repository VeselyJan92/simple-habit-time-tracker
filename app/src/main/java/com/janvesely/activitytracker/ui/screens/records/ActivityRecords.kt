package com.janvesely.activitytracker.ui.screens.records

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.SectionHeader
import com.janvesely.activitytracker.ui.components.Typography

@ExperimentalFocus
@ExperimentalFoundationApi
@Preview
@ExperimentalLayout
@Composable
fun TrackedActivityRecordsScreen(){
    Scaffold(
        topBar = {
            TopAppBar {
                Text(
                    "Aktivity",
                    Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                    style = Typography.AppTitle
                )
            }
        },
        bodyContent = {
            ScreenBody()
        },
        bottomBar = {

        },
        backgroundColor = Colors.AppBackground
    )
}

@Composable
fun ScreenBody() {
   Column {
       Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp).fillMaxWidth()) {

           Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
               ViewRange("DAILY", true)

               ViewRange("WEEKLY", false)

               ViewRange("MONTHLY", false)


               DateSelector()
           }

       }

       Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp).fillMaxWidth()) {
           Column(modifier = Modifier.padding(8.dp)) {
               SectionHeader("January")

               Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                   AgregateBlock("Total time", "00:00")
                   AgregateBlock("Sessions", "12")
                   AgregateBlock("Average", "00:00")
                   AgregateBlock("Min", "00:00")
                   AgregateBlock("MAx", "00:00")

               }

               Divider(Modifier.padding(8.dp))

               Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                   repeat(7){
                       Text(
                           modifier = Modifier.size(40.dp, 20.dp),
                           text = "PO",
                           style = TextStyle(
                               fontWeight = FontWeight.W600,
                               fontSize = 10.sp
                           ),
                           textAlign = TextAlign.Center
                       )
                   }

               }

               repeat(6){
                   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                       repeat(7){
                          // MetricBlock(MetricBlockData.mock, -1, )
                       }
                   }
               }
           }

       }

       //RecentActivity()
   }

}

@Composable
fun ViewRange(label: String, selected: Boolean ){
    val color = if (selected) Colors.ChipGraySelected else Colors.ChipGray

    Stack(
        modifier = Modifier.size(80.dp, 25.dp).background(color, RoundedCornerShape(50)),
    ){
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
fun DateSelector(){
    Row(Modifier.size(80.dp, 25.dp).background(Colors.ChipGray, RoundedCornerShape(50))) {
        Icon(Icons.Filled.CalendarToday, Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp))

        Text(
            "select",
            Modifier.align(Alignment.CenterVertically).weight(1f).padding(end = 5.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
fun AgregateBlock(label: String, metric: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = label, style = TextStyle(
            fontSize = 10.sp
        ))

        Stack(modifier = Modifier.size(60.dp, 20.dp).background(Colors.AppAccent, RoundedCornerShape(10.dp))){

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = metric,
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    fontSize = 10.sp
                )
            )
        }

    }

}



