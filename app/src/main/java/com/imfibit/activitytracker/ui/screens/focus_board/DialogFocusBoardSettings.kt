package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Topic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable


@Preview
@Composable
private fun Preview() {

   val edit = remember {
        mutableStateOf(true)
    }

    DialogFocusBoardSettings(
        edit, DevSeeder.getTags(), {}, {_, _ ->}, {}, {}
    )
}

@Composable
fun DialogFocusBoardSettings(
    display: MutableState<Boolean>,
    tags: List<FocusBoardItemTag>,
    reorderTags: () -> Unit,
    swapTags: (ItemPosition, ItemPosition) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
){
    BaseDialog(display = display ) {

        Column(modifier = Modifier.padding(8.dp)) {
            DialogBaseHeader(title = "Focus board settings")


            val dialogAdd = rememberDialog()

            DialogEditTag(
                item = FocusBoardItemTag(position = 1000),
                display = dialogAdd,
                isEdit = false,
                onTagEdit = onTagEdit
            )




        /*    Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = "Group items by first tag",
                    style = TextStyle.Default.copy(fontSize = 16.sp)
                )

                Switch(checked = true, onCheckedChange = {} )
            }*/

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Reorder and edit tags", style = AppTextFieldStyle_Header)

                FocusItemTag(
                    tagModifier = Modifier,
                    onClick = { dialogAdd.value = true },
                    name = "+ Add tag",
                    color = Colors.SuperLight
                )
            }

            val reorderState = rememberReorderableLazyListState(
                onDragEnd = {_, _-> reorderTags() },
                onMove = swapTags
            )

            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(tags, {it.id}){ item ->

                    val dialogEdit = rememberDialog()

                    DialogEditTag(
                        display = dialogEdit,
                        isEdit = true,
                        item = item,
                        onTagEdit = onTagEdit,
                        onTagDelete = onTagDelete
                    )

                   ReorderableItem(reorderableState = reorderState, key = item.id) {
                       FocusItemTag(
                           tagModifier = Modifier.fillMaxWidth(),
                           onClick = {
                               dialogEdit.value = true
                           },
                           name = item.name,
                           color = if (it) Color.LightGray else item.getUIColor()
                       )
                   }
                }
            }
        }

        DialogButtons {

            TextButton(
                onClick = { display.value = false},
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}