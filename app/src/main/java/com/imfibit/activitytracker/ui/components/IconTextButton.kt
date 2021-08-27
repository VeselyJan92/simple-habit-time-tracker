package com.imfibit.activitytracker.ui.components

import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Topic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconTextButton(icon: ImageVector, text: String?, modifier: Modifier = Modifier, onClick: ()->Unit){
    Row(
        modifier = modifier
            .width(80.dp)
            .height(30.dp)
            .background(Colors.ChipGray, RoundedCornerShape(50))
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
                .size(15.dp)
        )

        Text(
            text = text ?: "-",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 10.sp
            )
        )
    }

}