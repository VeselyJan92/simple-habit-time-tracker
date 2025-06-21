package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.EditText


@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun DialogAgree(
    display: MutableState<Boolean>,
    title: String,
    text: String? = null,
    noinline onAction: (Boolean) -> Unit
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = title)

        if (text != null)
            Text(text = text)

        DialogButtons {
            TextButton(onClick = {onAction.invoke(false)} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = {onAction.invoke(true)}) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}