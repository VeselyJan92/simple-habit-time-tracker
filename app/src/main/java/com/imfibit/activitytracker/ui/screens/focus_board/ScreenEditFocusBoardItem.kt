package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.dialogs.ConfirmDialog
import com.imfibit.activitytracker.ui.components.editor.RichContentEditor
import com.imfibit.activitytracker.ui.components.editor.RichContentEditorState
import com.imfibit.activitytracker.ui.components.editor.RichContentFormattingToolbar
import com.imfibit.activitytracker.ui.screens.focus_board.components.FocusItemTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenEditFocusBoardItem(bundleId: Long, noteId: Long) {
    val viewModel = hiltViewModel<ScreenEditFocusBoardItemViewModel, ScreenEditFocusBoardItemViewModel.Factory> {
        factory -> factory.create(bundleId, noteId)
    }

    val allTags by viewModel.tags.collectAsStateWithLifecycle()
    val itemMetadata by viewModel.item.collectAsStateWithLifecycle()
    val bundle by viewModel.bundle.collectAsStateWithLifecycle()

    val navigation = hiltViewModel<BackstackViewModel>()

    FullScreenEditFocusItem(
        onDismissRequest = {
            navigation.popBackStack()
        },
        bundleName = bundle?.title ?: stringResource(id = R.string.focus_board_bundles),
        titleState = viewModel.titleState,
        contentState = viewModel.contentState,
        allTags = allTags,
        selectedTags = itemMetadata?.tags ?: emptyList(),
        onToggleTag = viewModel::onToggleTag,
        isPinned = itemMetadata?.item?.isPinned ?: false,
        onTogglePin = viewModel::onTogglePin,
        isStarred = itemMetadata?.item?.isStarred ?: false,
        onToggleStar = viewModel::onToggleStar,
        onDelete = viewModel::onFocusItemDelete
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullScreenEditFocusItem(
    onDismissRequest: () -> Unit,
    bundleName: String,
    titleState: TextFieldState,
    contentState: RichContentEditorState,
    allTags: List<FocusBoardItemTag>,
    selectedTags: List<FocusBoardItemTag>,
    onToggleTag: (FocusBoardItemTag) -> Unit,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    isStarred: Boolean,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = stringResource(id = R.string.delete_confirmation_title),
            text = stringResource(id = R.string.focus_item_delete_confirm),
            onAction = { agree ->
                if (agree) {
                    onDelete()
                    onDismissRequest()
                }
            }
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.lightBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = bundleName,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.onSurfaceVariant
                            )
                        )
                        Text(
                            text = stringResource(id = R.string.focus_item_edited_just_now),
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = AppTheme.colors.outline
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.dialog_action_cancel), tint = AppTheme.colors.onSurfaceVariant)
                    }
                },
                actions = {
                    // Star Icon - Pin to main focus board
                    IconButton(onClick = onToggleStar) {
                        Icon(
                            imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = stringResource(id = R.string.focus_board_pinned),
                            tint = if (isStarred) AppTheme.colors.warning else AppTheme.colors.onSurfaceVariant // Amber color for star
                        )
                    }
                    // Pin Icon - Pin to bundle top
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = stringResource(id = R.string.focus_board_pinned),
                            tint = if (isPinned) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant
                        )
                    }
                    // Delete Icon
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(id = R.string.dialog_action_delete),
                            tint = AppTheme.colors.error // Red color for destructive action
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            RichContentFormattingToolbar(
                state = contentState,
                modifier = Modifier.imePadding()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // TITLE INPUT
            BasicTextField(
                state = titleState,
                textStyle = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = AppTheme.colors.onSurfaceTitle
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                cursorBrush = SolidColor(AppTheme.colors.primary),
                decorator = { innerTextField ->
                    if (titleState.text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.focus_item_title_placeholder),
                            style = TextStyle(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = AppTheme.colors.outlineVariant
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // HORIZONTAL TAGS SCROLL
            if (allTags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allTags.forEach { tag ->
                        val isSelected = selectedTags.any { it.id == tag.id }
                        FocusItemTag(
                            name = tag.name,
                            isSelected = isSelected,
                            color = Color(tag.color),
                            isSolidAlways = false,
                            onClick = { onToggleTag(tag) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REUSABLE RICH CONTENT EDITOR
            RichContentEditor(
                state = contentState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = stringResource(id = R.string.focus_board_edit_focus_item_content_placeholder)
            )

            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenEditFocusBoardItemPreview() = AppTheme {
    FullScreenEditFocusItem(
        onDismissRequest = {}, bundleName = "Nápady",
        titleState = TextFieldState("Title"),
        contentState = RichContentEditorState(DevSeeder.getMarkdownSample()),
        allTags = DevSeeder.getTags(),
        selectedTags = listOf(DevSeeder.getTags()[1]),
        onToggleTag = {},
        isPinned = false,
        onTogglePin = {},
        isStarred = false,
        onToggleStar = {},
        onDelete = {}
    )
}
