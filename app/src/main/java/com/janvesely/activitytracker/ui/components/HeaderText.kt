package com.janvesely.activitytracker.ui.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(label: String){
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = label,
        style = TextStyle(
            fontWeight = FontWeight.W600,
            fontSize = 20.sp
        ),
        textAlign = TextAlign.Center
    )
}