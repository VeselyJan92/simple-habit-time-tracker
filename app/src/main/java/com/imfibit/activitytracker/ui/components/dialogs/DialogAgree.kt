package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppTheme

@Preview
@Composable
fun DialogAgree() = AppTheme {
    DialogAgree(
        onDismissRequest = {},
        title = "Title",
        text = "Text",
        onAction = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogAgree(
    onDismissRequest: () -> Unit,
    title: String,
    text: String? = null,
    onAction: (Boolean) -> Unit,
) = BaseDialog(onDismissRequest = onDismissRequest) {

    DialogBaseHeader(title = title)

    if (text != null)
        Text(text = text)

    DialogButtons {
        TextButton(
            onClick = {
                onDismissRequest()
                onAction(false)
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                onDismissRequest()
                onAction(true)
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}
