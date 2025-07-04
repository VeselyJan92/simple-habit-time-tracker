package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun SectionHeader(label: String){
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp
        )
    )
}