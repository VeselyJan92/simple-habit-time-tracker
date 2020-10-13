package com.janvesely.activitytracker.ui.components.dialogs

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.janvesely.activitytracker.ui.components.Colors




@Composable
inline fun BaseDialog(display: MutableState<Boolean>, noinline content: @Composable ColumnScope.() -> Unit){
    if (display.value) Dialog(onDismissRequest = {display.value = false}) {

        Surface(
            elevation = 2.dp
        ){
            Column(children = content)
        }
    }
}

@Composable
fun DialogBaseHeader(title: String){
    Row(
        modifier = Modifier.fillMaxWidth().background(Colors.AppPrimary).padding(8.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        )
    }
}

@Composable
inline fun DialogButtons(content: @Composable RowScope.() -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        children = content,
        horizontalArrangement = Arrangement.End
    )
}


