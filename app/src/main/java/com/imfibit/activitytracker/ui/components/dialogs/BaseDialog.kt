package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.imfibit.activitytracker.ui.components.Colors




@Composable
inline fun BaseDialog(display: MutableState<Boolean>, noinline content: @Composable ColumnScope.() -> Unit){
    if (display.value) Dialog(onDismissRequest = {display.value = false}) {

        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            elevation = 2.dp
        ){
            Column(content = content)
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
        modifier = Modifier.fillMaxWidth().padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
        content = content,
        horizontalArrangement = Arrangement.End
    )
}


