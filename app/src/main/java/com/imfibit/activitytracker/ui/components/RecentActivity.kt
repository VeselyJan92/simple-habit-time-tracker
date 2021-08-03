package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.imfibit.activitytracker.database.entities.TrackedActivity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


data class Week(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val days: List<MetricWidgetData>,
    val stat: MetricWidgetData?
)

@Composable
fun RecentActivityGrid(weeks: List<Week>, nav: NavController){

    Column(Modifier.fillMaxWidth()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {


            DayOfWeek.values().forEach {
                Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center){
                    Text(
                        text = it.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()).toUpperCase(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center){
                Icon(imageVector = Icons.Filled.Functions, contentDescription = null)
            }

        }

        for ( (index, week) in weeks.withIndex()){


            val split = week.from.monthValue != week.to.monthValue || week.from.dayOfMonth == 1 || week.to.dayOfMonth == 1

            if (weeks.size -1 != index && split){
                MonthSplitter(month = week.from.month.name)
            }

            Week(week, nav)

        }
    }

}

@Composable
private fun Week(week: Week, nav: NavController){
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.days.forEach {
            val modifier = if (it.editable!!.from.toLocalDate() == LocalDate.now())
                Modifier.border(width = 1.dp, Color.Black, shape = RoundedCornerShape(50))
            else
                Modifier

            MetricBlock(
                it,
                isEditable = true,
                onClick = {
                    if (it.editable!!.type == TrackedActivity.Type.CHECKED)
                        return@MetricBlock

                    val id = it.editable!!.activityId
                    val date = it.editable!!.from.format(DateTimeFormatter.ISO_DATE)

                    nav.navigate("screen_day_history/$id/$date")


                },
                modifier = modifier
            )
        }

        if (week.stat != null){
            Box(modifier = Modifier.size(1.dp, 30.dp).background(Colors.ChipGray))

            MetricBlock(week.stat, isEditable = false)
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