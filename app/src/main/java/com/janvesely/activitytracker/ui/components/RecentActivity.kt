package com.janvesely.activitytracker.ui.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.janvesely.activitytracker.database.composed.MetricBlockData
import com.janvesely.activitytracker.database.entities.TrackedActivity
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*


data class Week(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val days: List<BaseMetricData>,
    val stat: BaseMetricData?
)

@Composable
fun RecentActivityGrid(weeks: List<Week>){

    Column(Modifier.fillMaxWidth()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {


            DayOfWeek.values().forEach {
                Stack(Modifier.size(40.dp, 30.dp), alignment = Alignment.Center){
                    Text(
                        text = it.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).toUpperCase(),
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Stack(Modifier.size(40.dp, 30.dp), alignment = Alignment.Center){
                Icon(asset = Icons.Filled.Functions)
            }

        }

        for (week in weeks){

            Week(week)

            if (week.from.monthValue != week.to.monthValue || week.from.dayOfMonth == 1 || week.to.dayOfMonth == 1){
                MonthSplitter(month = week.from.month.name)
            }

        }
    }

}

@Composable
private fun Week(week: Week){
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.days.forEach {
            MetricBlock(it, editable = true)
        }

        if (week.stat != null){
            MetricBlock(week.stat, editable = false)
        }



    }

}

@Composable
private fun MonthSplitter(month: String){
    Row(Modifier.fillMaxWidth()) {
        Divider(Modifier.padding(8.dp).weight(1f))
        Text(text = month)
        Divider(Modifier.padding(8.dp).weight(1f))
    }
}