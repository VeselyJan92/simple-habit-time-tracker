package com.imfibit.activitytracker.ui.screens.activity_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.AppNotificationManager
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.Timer
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.selectors.MinuteAndHourSelector
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@Composable
fun LiveActivitiesList(vm: ActivitiesViewModel) {

    val items: List<TrackedActivity> by vm.live.observeAsState(listOf())

    LazyColumn(
        modifier = Modifier.clip(RoundedCornerShape(topLeft = 20.dp, topRight = 20.dp))
            .background(Color.White),
    ) {
        items(items) { LiveActivity(vm = vm, item = it) }
    }
}

@Composable
private fun LiveActivity(vm: ActivitiesViewModel, item: TrackedActivity) {



    Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {

        val context = AmbientContext.current

        IconButton(
            onClick = {
                vm.stopSession(context, item)
            }
        ) {
            Box(Modifier.size(20.dp, 20.dp).background(Color.Red))
        }

        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Timer(
            startTime = item.inSessionSince!!,
            onClick = {
                GlobalScope.launch {
                    vm.rep.activityDAO.update(item.copy(inSessionSince = it))
                }
            }
        )

    }
}




