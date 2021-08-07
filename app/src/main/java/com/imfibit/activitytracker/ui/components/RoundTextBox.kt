package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun RoundTextBox(modifier: Modifier, text: String, style: TextStyle = TextStyle.Default, color: Color = Colors.ChipGray){

    Box(modifier = modifier.background(color, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            modifier = Modifier,
            style = style
        )
    }

}