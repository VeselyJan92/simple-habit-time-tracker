package com.imfibit.activitytracker.ui.screens.activity_list

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.App
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity.Type
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITY
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricBlock
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.components.dialogs.DialogScore
import com.imfibit.activitytracker.ui.components.dialogs.DialogSession
import java.time.LocalDateTime


data class TrackedActivityWithMetric constructor(
    val activity: TrackedActivity,
    val past: List<MetricWidgetData>,
    val hasMetricToday: Boolean
) {
    fun currentCompleted() = past[0].editable!!.metric >= activity.goal.value
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackedActivitiesList(
    nav: NavHostController,
    vm: ActivitiesViewModel
) {
    val items by vm.activities.observeAsState(listOf())



    LazyColumn(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items.size) {
            TrackedActivity(vm = vm, item = items[it], nav = nav)
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}

@Composable
private fun TrackedActivity(
    vm: ActivitiesViewModel,
    item: TrackedActivityWithMetric,
    nav: NavHostController
) {
    val activity = item.activity

    val requestEdit = remember { mutableStateOf(false) }

    if (requestEdit.value) when (activity.type) {
        Type.TIME -> DialogSession(
            display = requestEdit,
            activityId = activity.id,
            from = LocalDateTime.now(),
            to = LocalDateTime.now(),
        )
        Type.SCORE -> DialogScore(
            display = requestEdit,
            activityId = activity.id,
            datetime = LocalDateTime.now(),
            score = 1
        )
    }


    Surface(
        modifier = Modifier.clickable(
            onClick = { nav.navigate(SCREEN_ACTIVITY(activity.id.toString())) }
        ).padding(2.dp),

        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White).padding(top = 8.dp, bottom = 8.dp, end = 8.dp)

        ) {

            ActionButton(
                vm = vm,
                hasMetricToday = item.hasMetricToday,
                activity = item.activity,
                requestEdit = requestEdit
            )


            Column(Modifier.fillMaxWidth()) {
                Row {
                    Text(
                        activity.name,
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if (activity.goal.isSet()) {
                        Goal(activity.type.getComposeString(activity.goal.value).invoke())
                    }

                }

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricBlock(item.past[0], isEditable = false, width = 80.dp, metricStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                    MetricBlock(item.past[1], isEditable = true)
                    MetricBlock(item.past[2], isEditable = true)
                    MetricBlock(item.past[3], isEditable = true)
                    MetricBlock(item.past[4], isEditable = true)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowScope.ActionButton(
    vm: ActivitiesViewModel,
    hasMetricToday: Boolean,
    activity: TrackedActivity,
    requestEdit: MutableState<Boolean>
) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.align(Alignment.CenterVertically)
            .padding(end = 8.dp, start = 8.dp)
            .combinedClickable(
                onClick = { vm.activityTriggered(activity, context) },
                onLongClick = {
                    val viber = App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    viber.vibrate(VibrationEffect.createOneShot(50L, 1))

                    if (activity.type != Type.CHECKED)
                        requestEdit.value = true
                }
            )
    ) {

        val icon = when (activity.type) {
            Type.TIME -> Icons.Filled.PlayArrow
            Type.SCORE -> Icons.Filled.Add
            Type.CHECKED -> if (hasMetricToday) Icons.Filled.DoneAll else Icons.Filled.Check
        }

        Icon(
            contentDescription = null,
            imageVector = icon,
            modifier = Modifier
                .size(34.dp)
                .background(Colors.AppAccent, RoundedCornerShape(17.dp))
        )
    }
}


@Composable
fun Goal(label: String) {
    Row(Modifier.size(70.dp, 20.dp).padding(end = 8.dp).background(Colors.ChipGray, RoundedCornerShape(50))) {
        Modifier.align(Alignment.CenterVertically).padding(start = 5.dp).size(15.dp)
        Icon(Icons.Filled.Flag, contentDescription = null,)

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



