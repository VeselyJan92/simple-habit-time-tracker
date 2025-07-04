package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun rememberDialog(): MutableState<Boolean> {
    return remember {
        mutableStateOf(false)
    }
}


@Composable
@Deprecated("")
inline fun BaseDialog(
    modifier: Modifier = Modifier,
    display: MutableState<Boolean>,
    noinline content: @Composable ColumnScope.() -> Unit,
) {
    if (display.value) Dialog(
        onDismissRequest = { display.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)

    ) {
        Surface(
            modifier = modifier.width(320.dp),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 2.dp,

            ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
inline fun BaseDialogV2(
    noinline onDismissRequest: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(top = 16.dp,  start = 16.dp, end = 16.dp),
    noinline content: @Composable() (ColumnScope.() -> Unit),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(paddingValues),
                content = content
            )
        }
    }
}

@Composable
fun DialogBaseHeader(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
inline fun DialogButtons(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        content = content,
        horizontalArrangement = Arrangement.End
    )
}


