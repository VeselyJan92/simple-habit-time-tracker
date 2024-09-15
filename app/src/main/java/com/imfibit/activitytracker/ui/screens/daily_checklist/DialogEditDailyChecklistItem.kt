package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.TestTag.DAILY_CHECKLIST_EDIT_CONTINUE
import com.imfibit.activitytracker.core.TestTag.DAILY_CHECKLIST_EDIT_DIALOG
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
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

    DialogEditDailyChecklistItem(
        edit, true, DevSeeder.getDailyChecklistItem(), {}, {}
    )
}

@Composable
fun DialogEditDailyChecklistItem(
    display: MutableState<Boolean>,
    isEdit: Boolean,
    item: DailyChecklistItem,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit = {},
) {
    BaseDialog(
        modifier = Modifier.testTag(DAILY_CHECKLIST_EDIT_DIALOG),
        display = display,
    ) {
        val dialogTitle =
            if (isEdit) R.string.daily_checklist_edit_item_title_edit else R.string.daily_checklist_edit_item_title_create

        DialogBaseHeader(title = stringResource(id = dialogTitle))

        var title by remember {
            mutableStateOf(TextFieldValue(item.title))
        }

        var content by remember {
            mutableStateOf(TextFieldValue(item.description))
        }

        var color by remember {
            mutableStateOf(item.color.toColor())
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTag.DAILY_CHECKLIST_EDIT_TITLE),
                value = title,
                onValueChange = { title = it },
                style = AppTextFieldStyle_Header,
                placeholderText = stringResource(R.string.daily_checklist_edit_item_title_edit)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp)
                    .testTag(TestTag.DAILY_CHECKLIST_EDIT_DESCRIPTION),
                value = content,
                onValueChange = { content = it },
                placeholderText = stringResource(R.string.daily_checklist_edit_item_content_placeholder),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            ColorPickerComponent(
                selected = color,
                onChoose = {
                    color = it
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        DialogButtons {

            if (isEdit) {
                TextButton(
                    modifier = Modifier.testTag(TestTag.DAILY_CHECKLIST_EDIT_DELETE),
                    onClick = {
                        onItemDelete(item)
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
                modifier = Modifier.testTag(DAILY_CHECKLIST_EDIT_CONTINUE),
                onClick = {
                    val editedItem = item.copy(
                        title = title.text,
                        description = content.text,
                        color = color.toArgb()
                    )

                    onItemEdit(editedItem)
                    display.value = false
                },
                enabled = content.text.isNotEmpty() || title.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}