package com.imfibit.activitytracker.ui.screens.daily_checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewBottomSheet(){
    val state = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded, skipHiddenState = true
    )

    EditDailyChecklistItemBottomSheet(
        state = state,
        onDismiss = { },
        isEdit = true,
        item = DevSeeder.getDailyChecklistItem(),
        onItemEdit = { /* Handle edit */ },
        onItemDelete = { /* Handle delete */ }
    )
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditDailyChecklistItemBottomSheet(
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    isEdit: Boolean,
    item: DailyChecklistItem,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit = {},
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismiss,
        modifier = Modifier.imePadding().displayCutoutPadding(),
        containerColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        content = {
            EditDailyChecklistItemContent(
                isEdit = isEdit,
                item = item,
                onItemEdit = onItemEdit,
                onItemDelete = onItemDelete,
                onDismiss = onDismiss
            )
        },
    )
}

@Composable
private fun EditDailyChecklistItemContent(
    isEdit: Boolean,
    item: DailyChecklistItem,
    onItemEdit: (DailyChecklistItem) -> Unit,
    onItemDelete: (DailyChecklistItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle =
        if (isEdit) R.string.daily_checklist_edit_item_title_edit else R.string.daily_checklist_edit_item_title_create

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
            .fillMaxWidth().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp) // Adjusted padding for bottom sheet
    ) {
        // You might want a handle for the bottom sheet
        // Box(
        // modifier = Modifier
        // .width(40.dp)
        // .height(4.dp)
        // .background(MaterialTheme.colors.onSurface.copy(alpha = 0.4f), shape = CircleShape)
        // .align(Alignment.CenterHorizontally)
        // )
        // Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = dialogTitle),
            style = MaterialTheme.typography.h6, // Example style
            modifier = Modifier.padding(bottom = 16.dp)
        )


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
                        onItemDelete(item)
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            TextButton(onClick = onDismiss) {
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

                    onItemEdit(editedItem)
                    onDismiss()
                },
                enabled = content.text.isNotEmpty() || title.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }
}