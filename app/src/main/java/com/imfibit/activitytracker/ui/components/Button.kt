package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(
    text: String,
    onclick: ()->Unit,
    width: Dp =  70.dp,
    height: Dp =  30.dp,
    color: Color = Colors.ChipGray
){
    Box(
        modifier = Modifier
            .size(width, height)
            .background(color, RoundedCornerShape(50))
            .clickable(onClick = onclick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}