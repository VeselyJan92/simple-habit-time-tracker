package com.imfibit.activitytracker.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R

@Composable
fun LabeledColumn(
    text: String,
    body: @Composable ColumnScope.()->Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            textAlign = TextAlign.Center,
            text = text,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
        )

        body.invoke(this)
    }

}