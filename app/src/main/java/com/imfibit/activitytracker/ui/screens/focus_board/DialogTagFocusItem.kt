package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent


@Preview
@Composable
private fun DialogEditTag_Preview() = AppTheme {
    DialogEditTag(
        onDismissRequest = {  },
        isEdit = true,
        item = DevSeeder.getFocusBoardItemTag(),
        onTagEdit = {},
        onTagDelete = {}
    )
}

@Composable
fun DialogEditTag(
    onDismissRequest: () -> Unit,
    isEdit: Boolean,
    item: FocusBoardItemTag = FocusBoardItemTag(),
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit = {},
) = BaseDialog(onDismissRequest = onDismissRequest) {

    DialogBaseHeader(title = "Focus item label")

    var color by remember {
        mutableStateOf(item.color.toColor())
    }

    var name by remember {
        mutableStateOf(TextFieldValue(item.name))
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

        Spacer(modifier = Modifier.height(8.dp))

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
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }
        }

        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                val editedItem = item.copy(name = name.text, color = color.toArgb())

                onTagEdit(editedItem)
                onDismissRequest()
            },
            enabled = name.text.isNotEmpty()
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}
