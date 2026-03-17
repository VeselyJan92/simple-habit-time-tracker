package com.imfibit.activitytracker.ui.screens.focus_board.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent


@Preview
@Composable
private fun DialogEditFocusItemTag_Preview() = AppTheme {
    DialogEditFocusItemTag(
        onDismissRequest = {  },
        isEdit = true,
        item = DevSeeder.getFocusBoardItemTag(),
        onTagEdit = {},
        onTagDelete = {}
    )
}

@Composable
fun DialogEditFocusItemTag(
    onDismissRequest: () -> Unit,
    isEdit: Boolean,
    item: FocusBoardItemTag = FocusBoardItemTag(),
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit = {},
) = BaseDialog(onDismissRequest = onDismissRequest) {

    DialogBaseHeader(title = stringResource(id = if (isEdit) R.string.dialog_edit_tag_title else R.string.dialog_create_tag_title))

    var color by remember {
        mutableStateOf(item.color.toColor())
    }

    var name by remember {
        mutableStateOf(TextFieldValue(item.name))
    }

    var isTaskTag by remember {
        mutableStateOf(item.isTaskTag)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = {
                Text(stringResource(R.string.focus_board_edit_tag_name_placeholder))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.dialog_edit_tag_task_tag), style = MaterialTheme.typography.titleMedium, color = AppTheme.colors.onSurface)
                Text(text = stringResource(id = R.string.dialog_edit_tag_hint), style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
            }
            Switch(checked = isTaskTag, onCheckedChange = { isTaskTag = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        ColorPickerComponent(
            selected = color,
            onChoose = {
                color = it
            }
        )

    }

    DialogButtons {

        if (isEdit) {
            TextButton(
                onClick = {
                    onTagDelete(item)
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_delete), color = AppTheme.colors.error)
            }
        }

        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                val editedItem = item.copy(
                    name = name.text, 
                    color = color.toArgb(),
                    isTaskTag = isTaskTag
                )

                onTagEdit(editedItem)
                onDismissRequest()
            },
            enabled = name.text.isNotEmpty()
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}
