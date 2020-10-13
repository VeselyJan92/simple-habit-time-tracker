package com.janvesely.activitytracker.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.entities.TrackedActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

            Week(week, nav)

            if (week.from.monthValue != week.to.monthValue || week.from.dayOfMonth == 1 || week.to.dayOfMonth == 1){
                MonthSplitter(month = week.from.month.name)
            }

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

                    nav.navigate(
                        R.id.action_activity_fragment_to_fragment_day_records,
                        bundleOf(
                            "id" to it.editable!!.activityId,
                            "date" to it.editable!!.from.format(DateTimeFormatter.ISO_DATE)
                        )
                    )
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