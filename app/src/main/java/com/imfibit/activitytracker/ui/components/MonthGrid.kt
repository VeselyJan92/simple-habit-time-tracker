package com.imfibit.activitytracker.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import java.time.*
import java.time.format.TextStyle
import java.util.*


@Composable
fun Month(
    modifier: Modifier = Modifier,
    activity: TrackedActivity,
    month: RepositoryTrackedActivity.Month,
    nav: NavController
){
    MonthSplitter(month =  "${month.month.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).uppercase()} - ${month.month.year}")

    Column(modifier = modifier.padding(3.dp)) {
        month.weeks.forEach {
            Week(activity, it, nav, month.month.month)
        }

    }
}

@Composable
private fun Week(activity: TrackedActivity, week: RepositoryTrackedActivity.Week, nav: NavController, month: Month){
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        week.days.forEach {
            val modifier = if (it.date == LocalDate.now())
                Modifier.border(width = 2.dp, Color.Black, shape = RoundedCornerShape(50))
            else
                Modifier

            val context = LocalContext.current
            val requestEdit = remember { mutableStateOf(false) }
            val vm = hiltViewModel<RecordViewModel>()

            if (requestEdit.value ) when (it.type){
                TrackedActivity.Type.TIME -> DialogSession(
                    display = requestEdit,
                    record = TrackedActivityTime(0, activity.id, it.date.atTime(12, 0), it.date.atTime(13, 0)),
                    onUpdate = { from, to -> vm.insertSession(activity.id, from, to) },
                )

                TrackedActivity.Type.SCORE  -> DialogScore(
                    display = requestEdit,
                    record = TrackedActivityScore(0, activity.id, it.date.atTime(12, 0), 1),
                    onUpdate = {time, score -> vm.addScore(activity.id, time, score) },
                )

                else -> {}
            }


            if(it.date.month == month){
                MetricBlock(
                    data = MetricWidgetData(it.type.getComposeString(it.metric), it.color, it.label),
                    onClick = {
                        nav.navigate("screen_day_history/${activity.id}/${it.date}")
                    },
                    onLongClick = {
                        val viber = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        viber.vibrate(VibrationEffect.createOneShot(50L, 1))

                          when(it.type){
                              TrackedActivity.Type.TIME, TrackedActivity.Type.SCORE -> requestEdit.value = true
                              TrackedActivity.Type.CHECKED ->  vm.toggleHabit(activity.id, it.date.atTime(LocalTime.now()))
                          }

                    },
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

        if (week.to.month == month || week.to >= LocalDateTime.now() ){
            val metric:ComposeString = when (activity.type) {
                TrackedActivity.Type.CHECKED -> {{ "${week.total}/7" }}
                else -> activity.type.getComposeString(week.total)
            }

            MetricBlock(MetricWidgetData(metric, activity.goal.color(week.total), { stringResource(id = R.string.week)}))
        }else{
            MetricBlock(MetricWidgetData({"-"}, Color.LightGray, {""}))
        }

    }

}

@Composable
private fun MonthSplitter(month: String){
    Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
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