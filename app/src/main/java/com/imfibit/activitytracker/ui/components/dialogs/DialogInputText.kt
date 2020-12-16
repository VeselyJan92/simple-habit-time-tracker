package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.components.EditText


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
inline fun DialogInputText(
    display: MutableState<Boolean>,
    text: String,
    title: String,
    noinline onTextSet: (String) -> Unit
){

    BaseDialog(display = display ) {

        var text by remember { mutableStateOf(TextFieldValue(text)) }

        DialogBaseHeader(title = title)


        EditText(text, { text = it}, modifier = Modifier.padding(8.dp).height(30.dp))

        DialogButtons {
            TextButton(onClick = {display.value = false} ) {
                Text(text = "ZPĚT")
            }

            TextButton(onClick = {onTextSet.invoke(text.text)}) {
                Text(text = "POKRAČOVAT")
            }
        }
    }

}