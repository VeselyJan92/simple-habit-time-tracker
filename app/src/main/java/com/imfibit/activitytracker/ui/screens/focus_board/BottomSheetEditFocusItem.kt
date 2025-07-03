package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.AppTextField
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState


@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
private fun DialogEditFocusItem_Preview() = AppTheme {
    BottomSheetEditFocusItem(
        onDismissRequest = {},
        state = rememberTestBottomSheetState(),
        isEdit = true,
        item = DevSeeder.getFocusItemWithTags(),
        tags = DevSeeder.getTags(),
        onFocusItemEdit = {},
        onFocusItemDelete = {}
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetEditFocusItem(
    onDismissRequest: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    isEdit: Boolean,
    item: FocusBoardItemWithTags,
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit = {},
) {
    BaseBottomSheet(
        onDismissRequest = onDismissRequest,
        state = state
    ) { onDismissRequest ->

        val tagState = remember {
            mutableStateListOf(*item.tags.toTypedArray())
        }

        val title = remember {
            mutableStateOf(TextFieldValue(item.item.title))
        }

        val content = remember {
            mutableStateOf(TextFieldValue(item.item.content))
        }

        Text(
            text = stringResource(R.string.focus_board_edit_focus_item_title),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            value = title.value,
            onValueChange = { title.value = it },
            label = {
                Text(stringResource(R.string.focus_board_edit_focus_item_title_placeholder))
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            value = content.value,
            onValueChange = { content.value = it },
            label = {
                Text(stringResource(R.string.focus_board_edit_focus_item_content_placeholder))
            },
            singleLine = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            tags.forEach {
                FocusItemTag(
                    onClick = {
                        if (tagState.contains(it)) {
                            tagState.remove(it)
                        } else {
                            tagState.add(it)
                        }
                    },
                    isSelected = tagState.contains(it),
                    name = it.name,
                    color = it.getUIColor()
                )
            }
        }

        DialogButtons {
            if (isEdit) {
                TextButton(
                    onClick = {
                        onDismissRequest {
                            onFocusItemDelete(item)
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
            }

            TextButton(onClick = { onDismissRequest(null) }) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(
                onClick = {
                    val editedItem = item.item.copy(
                        title = title.value.text,
                        content = content.value.text,
                    )

                    onDismissRequest {
                        onFocusItemEdit(FocusBoardItemWithTags(editedItem, tagState.toList()))
                    }
                },
                enabled = content.value.text.isNotEmpty() || title.value.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }
}

