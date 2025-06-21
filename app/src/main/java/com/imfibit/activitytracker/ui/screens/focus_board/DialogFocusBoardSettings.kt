package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.swap
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Preview
@Composable
private fun Preview() {

    val edit = remember {
        mutableStateOf(true)
    }

    DialogFocusBoardSettings(
        edit, DevSeeder.getTags(), { _, _ -> }, {}, {}
    )
}

@Composable
fun DialogFocusBoardSettings(
    display: MutableState<Boolean>,
    tags: List<FocusBoardItemTag>,
    swapTags: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
) {
    BaseDialog(display = display) {

        Column(modifier = Modifier.padding(8.dp)) {
            DialogBaseHeader(title = "Focus board settings")

            val dialogAdd = rememberDialog()

            DialogEditTag(
                item = FocusBoardItemTag(position = 1000),
                display = dialogAdd,
                isEdit = false,
                onTagEdit = onTagEdit
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Reorder and edit tags", style = AppTextFieldStyle_Header
                )

                FocusItemTag(
                    modifier = Modifier,
                    onClick = { dialogAdd.value = true },
                    name = "+ Add tag",
                    color = Colors.SuperLight
                )
            }

            val lazyListState = rememberLazyListState()
            val reorderableLazyListState =
                rememberReorderableLazyListState(lazyListState) { from, to ->
                    swapTags(from, to)
                }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tags,
                    key = { it.id }
                ) { item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.id
                    ) {
                        val dialogEdit = rememberDialog()

                        DialogEditTag(
                            display = dialogEdit,
                            isEdit = true,
                            item = item,
                            onTagEdit = onTagEdit,
                            onTagDelete = onTagDelete
                        )

                        FocusItemTag(
                            modifier = Modifier
                                .fillMaxWidth()
                                .longPressDraggableHandle(),
                            onClick = {
                                dialogEdit.value = true
                            },
                            name = item.name,
                            color = if (false) Color.LightGray else item.getUIColor()
                        )
                    }
                }
            }
        }

        DialogButtons {

            TextButton(
                onClick = { display.value = false },
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }
}
