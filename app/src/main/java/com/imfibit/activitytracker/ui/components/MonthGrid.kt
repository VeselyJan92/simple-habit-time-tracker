package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import java.time.*
import java.time.format.TextStyle
import java.util.*


@Composable
fun Month(
    modifier: Modifier = Modifier,
    activity: TrackedActivity,
    month: RepositoryTrackedActivity.Month,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit
){
    MonthSplitter(month =  "${month.month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).uppercase()} - ${month.month.year}")

    Column(modifier = modifier.padding(3.dp)) {
        month.weeks.forEach {
            Week(activity, it, month.month.month, onDayClicked, onDayLongClicked)
        }
    }
}

@Composable
private fun Week(
    activity: TrackedActivity,
    week: RepositoryTrackedActivity.Week,
    month: Month,
    onDayClicked: (TrackedActivity, LocalDate) -> Unit,
    onDayLongClicked: (TrackedActivity, LocalDate) -> Unit

){
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.days.forEach {
            val modifier = if (it.date == LocalDate.now())
                Modifier.border(width = 2.dp, Color.Black, shape = RoundedCornerShape(50))
            else
                Modifier




            if(it.date.month == month){
                MetricBlock(
                    data = MetricWidgetData(it.type.getLabel(it.metric), it.color, it.label),
                    onClick = {
                        onDayClicked(activity, it.date)
                    },
                    onLongClick = { onDayLongClicked(activity, it.date) },
                    modifier = modifier
                )
            }else{
                Spacer(modifier = Modifier
                    .width(40.dp)
                    .height(20.dp))
            }
        }

        Box(modifier = Modifier
            .size(1.dp, 30.dp)
            .background(Colors.ChipGray))

        val currentMonth = LocalDate.now().month
        val showMetricForCurrentWeek = week.from.month == currentMonth && week.to.month != currentMonth

        if (week.to.month == month || showMetricForCurrentWeek ){
            val metric:ContextString = when (activity.type) {
                TrackedActivity.Type.CHECKED -> {{ "${week.total}/7" }}
                else -> activity.type.getLabel(week.total)
            }

            MetricBlock(MetricWidgetData(metric, activity.goal.color(week.total), { resources.getString(R.string.week)}))
        }else{
            MetricBlock(MetricWidgetData({"-"}, Color.LightGray, {""}))
        }

    }

}

@Composable
private fun MonthSplitter(month: String){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)) {
        Divider(
            Modifier
                .padding(8.dp)
                .weight(1f))
        Text(text = month, fontWeight = FontWeight.W600)
        Divider(
            Modifier
                .padding(8.dp)
                .weight(1f))
    }
}