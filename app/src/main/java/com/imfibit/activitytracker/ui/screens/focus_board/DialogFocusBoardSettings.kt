package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DialogFocusBoardSettings_Preview() = AppTheme {
    DialogFocusBoardSettings(
        state = rememberTestBottomSheetState(),
        onDismissRequest = {},
        tags = DevSeeder.getTags(),
        swapTags = { _, _ -> },
        onTagEdit = {},
        onTagDelete = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogFocusBoardSettings(
    state: SheetState = rememberAppBottomSheetState(),
    onDismissRequest: () -> Unit,
    tags: List<FocusBoardItemTag>,
    swapTags: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
) = BaseBottomSheet(
    paddingValues = PaddingValues(0.dp),
    state = state,
    onDismissRequest = onDismissRequest
) { onDismissRequest ->

    var addDialog by remember { mutableStateOf(false) }

    if (addDialog){
        DialogEditTag(
            onDismissRequest = { addDialog = false },
            item = FocusBoardItemTag(position = 1000),
            isEdit = false,
            onTagEdit = onTagEdit
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Focus item labels",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        )

        FocusItemTag(
            modifier = Modifier,
            iconStart = Icons.Default.Add,
            onClick = { addDialog = true },
            name = "Label",
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
        modifier = Modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
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
                var editDialog by remember { mutableStateOf(false) }

                if (editDialog){
                    DialogEditTag(
                        onDismissRequest = { editDialog = false },
                        item = item,
                        isEdit = false,
                        onTagDelete = onTagDelete,
                        onTagEdit = onTagEdit
                    )
                }

                FocusItemTag(
                    modifier = Modifier
                        .fillMaxWidth()
                        .longPressDraggableHandle(),
                    onClick = {
                        editDialog = true
                    },
                    iconAfter = Icons.Default.Reorder,
                    name = item.name,
                    color = if (false) Color.LightGray else item.getUIColor()
                )
            }
        }
    }

    DialogButtons {
        TextButton(
            onClick = { onDismissRequest(null) },
        ) {
            Text(text = "DONE")
        }
    }
}

