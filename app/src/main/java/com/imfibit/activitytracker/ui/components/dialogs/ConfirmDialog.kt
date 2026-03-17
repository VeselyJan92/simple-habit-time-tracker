package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppTheme

@Preview
@Composable
fun ConfirmDialogPreview() = AppTheme {
    ConfirmDialog(
        onDismissRequest = {},
        title = "Title",
        text = null,
        onAction = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String? = null,
    onAction: (Boolean) -> Unit,
) = BaseDialog(onDismissRequest = onDismissRequest) {

    DialogBaseHeader(title = title)

    if (text != null)
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = AppTheme.colors.onSurfaceVariant
        )

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
