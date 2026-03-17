package com.imfibit.activitytracker.ui.screens.focus_board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBundle
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.BaseMetricBlock
import com.imfibit.activitytracker.ui.components.BaseBottomSheet
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.ConfirmDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.components.selectors.ColorPickerComponent
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BottomSheetBundleSettings(
    state: SheetState = rememberAppBottomSheetState(),
    onDismissRequest: () -> Unit,
    bundle: FocusBundle,
    onBundleSave: (FocusBundle, List<FocusBoardItemTag>) -> Unit,
    onBundleDelete: (FocusBundle) -> Unit = {},
    tags: List<FocusBoardItemTag> = emptyList(),
) = BaseBottomSheet(
    paddingValues = PaddingValues(0.dp),
    state = state,
    onDismissRequest = onDismissRequest
) { onDismiss ->
    val isEdit = bundle.id != 0L

    var title by remember { mutableStateOf(bundle.title) }
    var selectedColor by remember { mutableLongStateOf(bundle.color) }

    val localTags = remember { mutableStateListOf<FocusBoardItemTag>().apply { addAll(tags) } }

    var addTagDialog by remember { mutableStateOf(false) }
    if (addTagDialog) {
        DialogEditFocusItemTag(
            onDismissRequest = { addTagDialog = false },
            item = FocusBoardItemTag(bundleId = bundle.id, position = localTags.size),
            isEdit = false,
            onTagEdit = { localTags.add(it) }
        )
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    if (showDeleteConfirmation) {
        ConfirmDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = stringResource(id = R.string.delete_confirmation_title),
            text = stringResource(id = R.string.delete_confirmation_message),
            onAction = { confirmed ->
                if (confirmed) {
                    onDismiss { onBundleDelete(bundle) }
                }
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localTags.apply {
            val fromIndex = from.index - 1
            val toIndex = to.index - 1
            if (fromIndex in indices && toIndex in indices) {
                val temp = this[fromIndex]
                this[fromIndex] = this[toIndex]
                this[toIndex] = temp
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "bundle_settings_header") {
            Text(
                text = stringResource(id = if (isEdit) R.string.focus_bundle_settings_title else R.string.focus_bundle_create_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.focus_bundle_title_label)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorPickerComponent(
                selected = Color(selectedColor),
                onChoose = {
                    selectedColor = it.toArgb().toLong()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.focus_bundle_settings_labels),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.colors.onSurface
                    )
                )

                IconButton(
                    onClick = { addTagDialog = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = stringResource(id = R.string.dialog_create_tag_title),
                    )

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (localTags.isEmpty()) {
            item {
                Surface(
                    color = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.focus_bundle_settings_no_labels),
                            style = MaterialTheme.typography.titleSmall,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(id = R.string.focus_bundle_settings_use_labels),
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        items(
            items = localTags,
            key = { if (it.id == 0L) it.hashCode() else it.id }
        ) { item ->
            ReorderableItem(
                state = reorderableState,
                key = if (item.id == 0L) item.hashCode() else item.id
            ) {
                var editDialog by remember { mutableStateOf(false) }

                if (editDialog) {
                    DialogEditFocusItemTag(
                        onDismissRequest = { editDialog = false },
                        item = item,
                        isEdit = true, // We are editing an item that is already in the list
                        onTagDelete = { tag -> localTags.removeIf { it === tag } },
                        onTagEdit = { tag ->
                            val index = localTags.indexOfFirst { it === item }
                            if (index != -1) localTags[index] = tag
                        }
                    )
                }

                BundleLabelItem(
                    item = item,
                    modifier = Modifier.fillMaxWidth().longPressDraggableHandle(),
                    onClick = { editDialog = true }
                )
            }
        }
    }

    DialogButtons(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (isEdit) {
            TextButton(
                onClick = {
                    showDeleteConfirmation = true
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_delete), color = AppTheme.colors.error)
            }
        }

        TextButton(onClick = { onDismiss(null) }) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                val editedBundle = bundle.copy(
                    title = title,
                    color = selectedColor,
                )

                onBundleSave(editedBundle, localTags.toList())

                onDismiss(null)
            },
            enabled = title.isNotEmpty()
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}

@Composable
private fun BundleLabelItem(
    item: FocusBoardItemTag,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AppTheme.colors.surfaceVariant.copy(alpha = 0.5f)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color line
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(Color(item.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = stringResource(id = R.string.screen_group_reorder),
                modifier = Modifier.padding(end = 16.dp),
                tint = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewBottomSheetBundleSettingsEdit() = AppTheme {
    BottomSheetBundleSettings(
        onDismissRequest = {},
        bundle = FocusBundle(
            id = 1L,
            title = "My Bundle",
            color = Colors.chooseableColors[2].toArgb().toLong()
        ),
        onBundleSave = { _, _ -> },
        onBundleDelete = {},
        tags = listOf(
            FocusBoardItemTag(id = 1, name = "Urgent", color = Colors.chooseableColors[0].toArgb()),
            FocusBoardItemTag(id = 2, name = "Later", color = Colors.chooseableColors[4].toArgb())
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewBottomSheetBundleSettingsCreate() = AppTheme {
    BottomSheetBundleSettings(
        onDismissRequest = {},
        bundle = FocusBundle(title = "", color = Colors.chooseableColors[0].toArgb().toLong()),
        onBundleSave = { _, _ -> }
    )
}
