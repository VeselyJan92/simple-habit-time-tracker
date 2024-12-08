package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.components.AppTextField
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent


@Preview()
@Composable
private fun Preview() {

    val edit = remember {
        mutableStateOf(true)
    }

    DialogEditTag(
        edit, true, DevSeeder.getFocusBoardItemTag(), {}, {}
    )
}

@Composable
fun DialogEditTag(
    display: MutableState<Boolean>,
    isEdit: Boolean,
    item: FocusBoardItemTag = FocusBoardItemTag(),
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit = {},
) {
    BaseDialog(display = display) {
        val dialogTitle = if (isEdit) R.string.dialog_edit_tag_title else R.string.dialog_create_tag_title

        DialogBaseHeader(title = stringResource(id = dialogTitle))

        var color by remember {
            mutableStateOf(item.color.toColor())
        }

        var name by remember {
            mutableStateOf(TextFieldValue(item.name))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                style = AppTextFieldStyle_Header,
                placeholderText = stringResource(R.string.focus_board_edit_tag_name_placeholder)
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
                        display.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
            }

            TextButton(onClick = { display.value = false }) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(
                onClick = {
                    val editedItem = item.copy(name = name.text, color = color.toArgb())

                    onTagEdit(editedItem)
                    display.value = false
                },
                enabled = name.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }
}