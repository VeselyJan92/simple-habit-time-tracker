package com.imfibit.activitytracker.ui.screens.focus_board

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.components.AppTextField
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons


@Preview()
@Composable
private fun Preview() {

   val edit = remember {
        mutableStateOf(true)
    }

    DialogEditFocusItem(
        edit, true, DevSeeder.getFocusItemWithTags(), DevSeeder.getTags(), {}, {}
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogEditFocusItem(
    display: MutableState<Boolean>,
    isEdit: Boolean,
    item: FocusBoardItemWithTags,
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags)->Unit,
    onFocusItemDelete: (FocusBoardItemWithTags)->Unit = {},

){
    BaseDialog(display = display ) {
        val dialogTitle = if (isEdit) R.string.dialog_edt_focus_item_title else R.string.dialog_create_focus_item_title

        DialogBaseHeader(title = stringResource(id = dialogTitle))

        val tagState = remember {
            mutableStateListOf(*item.tags.toTypedArray())
        }

        val title = remember {
            mutableStateOf(TextFieldValue(item.item.title))
        }

        val content = remember {
            mutableStateOf(TextFieldValue(item.item.content))
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)){

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title.value,
                onValueChange = {title.value = it},
                style = AppTextFieldStyle_Header,
                placeholderText = stringResource(R.string.focus_board_edit_focus_item_title_placeholder)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                value = content.value,
                onValueChange = {content.value = it},
                placeholderText = stringResource(R.string.focus_board_edit_focus_item_content_placeholder),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                tags.forEach {
                    FocusItemTag(
                        onClick = {
                            if (tagState.contains(it)){
                                tagState.remove(it)
                            }else{
                                tagState.add(it)
                            }
                        },
                        isSelected = tagState.contains(it),
                        name = it.name,
                        color = it.getUIColor()
                    )
                }
            }
        }

        DialogButtons {

            if(isEdit){
                TextButton(
                    onClick = {
                        onFocusItemDelete(item)
                        display.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
            }

            TextButton(onClick = { display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(
                onClick = {
                    val editedItem = item.item.copy(
                        title = title.value.text,
                        content = content.value.text,
                    )


                    onFocusItemEdit(FocusBoardItemWithTags(editedItem, tagState.toList()))
                    display.value = false
                },
                enabled = content.value.text.isNotEmpty() || title.value.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}