package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.ui.components.AppTextField
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewBottomSheet() {
    EditDailyChecklistItemBottomSheet(
        state = rememberTestBottomSheetState(),
        onDismissRequest = { },
        isEdit = true,
        item = DevSeeder.getDailyChecklistItem(),
        onItemEdit = { },
        onItemDelete = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDailyChecklistItemBottomSheet(
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismissRequest: () -> Unit,
    isEdit: Boolean,
    item: DailyChecklistItem,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit = {},
) = BaseBottomSheet(
    state = state,
    onDismissRequest = onDismissRequest,
    content = { onDismissRequest ->
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
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp) // Adjusted padding for bottom sheet
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

            Spacer(modifier = Modifier.height(16.dp)) // Adjusted spacing

            // Buttons arranged differently for a bottom sheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isEdit) {
                    TextButton(
                        modifier = Modifier.testTag(TestTag.DAILY_CHECKLIST_EDIT_DELETE),
                        onClick = {
                            onDismissRequest {
                                onItemDelete(item)
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.dialog_action_delete))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                TextButton(
                    onClick = {
                        onDismissRequest(null)
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    modifier = Modifier.testTag(DAILY_CHECKLIST_EDIT_CONTINUE),
                    onClick = {
                        val editedItem = item.copy(
                            title = title.text,
                            description = content.text,
                            color = color.toArgb()
                        )

                        onDismissRequest {
                            onItemEdit(editedItem)
                        }
                    },
                    enabled = content.text.isNotEmpty() || title.text.isNotEmpty()
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_continue))
                }
            }
        }
    }
)
