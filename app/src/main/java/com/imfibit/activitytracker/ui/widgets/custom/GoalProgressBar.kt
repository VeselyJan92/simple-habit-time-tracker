package com.imfibit.activitytracker.ui.widgets.custom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors

@Composable
fun GoalProgressBar(challenge: TrackedActivityChallenge, actual: Long, type: TrackedActivity.Type) = GoalProgressBar(
    name =  challenge.name,
    target = challenge.target,
    actual = actual,
    type = type
)

@Composable
fun GoalProgressBar(
    name: String,
    target: Long,
    actual: Long,
    type: TrackedActivity.Type
){

    val animationTargetState = remember { mutableStateOf(0f) }

    LaunchedEffect(target, actual){
        animationTargetState.value = if (actual == 0L || target == 0L) 0f else minOf((actual.toFloat() / target.toFloat()), 1f)
    }

    val animated by animateFloatAsState(
        targetValue = animationTargetState.value,
        animationSpec = tween(1000)
    )

    Box(
        Modifier
            .fillMaxWidth()
            .height(50.dp), contentAlignment = Alignment.Center,) {

        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)){
            drawRoundRect(
                size = Size(size.width, size.height),
                color = Color.LightGray,
                cornerRadius = CornerRadius(size.height, size.height),
                style = Stroke(5f)
            )

            val clamped  = if (animated * size.width < size.height) size.height else animated * size.width

            drawRoundRect(
                size = Size(clamped, size.height ),
                color = if (actual >= target && target != 0L) Colors.Completed else Color(0xFFE0E0E0),
                cornerRadius = CornerRadius(size.width, size.width)
            )
        }

        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){

            Text(name, fontWeight = FontWeight.W600, fontSize = 15.sp )

            val fractionLabel = if (actual > target && target != 0L){
                stringResource(R.string.dialog_challenge_estimated_completed)
            }else{
                when(type){
                    TrackedActivity.Type.TIME -> "${actual / 3600}h / ${target / 3600}h"
                    TrackedActivity.Type.SCORE, TrackedActivity.Type.CHECKED -> "$actual / ${target}"
                }
            }

            Text(fractionLabel, fontWeight = FontWeight.W600, fontSize = 17.sp )

        }
    }
}
