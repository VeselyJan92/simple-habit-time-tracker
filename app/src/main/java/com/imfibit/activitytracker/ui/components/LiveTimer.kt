package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.TimeUtils
import com.imfibit.activitytracker.ui.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime

@Preview
@Composable
fun Timer_Preview() = AppTheme {
    LiveTimer(LocalDateTime.now().minusMinutes(30))
}


@Composable
fun LiveTimer(
    startTime: LocalDateTime?,
) {
    val startTime = when {
        startTime == null -> null
        startTime > LocalDateTime.now() -> null
        else -> startTime
    }

    var time by remember { mutableStateOf(LocalDateTime.now()) }


    LaunchedEffect(Unit) {
        while (this.coroutineContext.isActive) {
            time = LocalDateTime.now()
            delay(1000)
        }
    }

    val text = if (startTime != null) {
        TimeUtils.secondsToMetric(startTime, time)
    } else {
        TimeUtils.secondsToMetric(0L)
    }

    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    )
}


@Composable
fun TimerBlock(
    modifier: Modifier = Modifier,
    startTime: LocalDateTime?,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(Colors.ChipGray, RoundedCornerShape(10.dp))
            .padding(4.dp)
            .clickable(onClick = {
                onClick?.invoke()
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Timer, contentDescription = null)

        Spacer(modifier = Modifier.width(4.dp))

        LiveTimer(startTime)
    }
}