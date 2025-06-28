package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.toggle
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.MainBody
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

val colors100 = listOf(
    Color(0xFFE1BEE7),
    Color(0xFFD1C4E9),
    Color(0xFFC5CAE9),
    Color(0xFFBBDEFB),
    Color(0xFFB3E5FC),
    Color(0xFFB2DFDB),
    Color(0xFFC8E6C9),
    Color(0xFFDCEDC8),
    Color(0xFFF0F4C3),
    Color(0xFFFFF9C4),
    Color(0xFFFFECB3),
)

val colors200 = listOf(
    Color(0xFFCE93D8),
    Color(0xFFB39DDB),
    Color(0xFF9FA8DA),
    Color(0xFF90CAF9),
    Color(0xFF81D4FA),
    Color(0xFF80DEEA),
    Color(0xFF80CBC4),
    Color(0xFFA5D6A7),
    Color(0xFFC5E1A5),
    Color(0xFFE6EE9C),
    Color(0xFFFFF59D),
    Color(0xFFFFE082),
    Color(0xFFFFCC80),
    Color(0xFFBCAAA4),
    Color(0xFFBCAAA4),
    Color(0xFFB0BEC5),
)

@Preview
@Composable
private fun ScreenFocusBoard_Preview() = AppTheme {
    val tags = listOf(DevSeeder.getFocusBoardItemTag())

    Body(
        items = listOf(
            FocusBoardItemWithTags(
                DevSeeder.getFocusBoardItem(),
                tags
            )
        ),
        tags = tags,
        onFocusItemEdit = {},
        onFocusItemDelete = {},
        swapFocusItems = { _, _ -> },
        swapTags = { _, _ -> },
        onTagEdit = {},
        onTagDelete = {}
    )
}


@Composable
fun ScreenFocusBoard(
    viewModel: FocusBoardViewModel = hiltViewModel<FocusBoardViewModel>(),
) {
    val focusItems by viewModel.focusItems.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()

    Body(
        items = focusItems,
        tags = tags,
        onFocusItemEdit = viewModel::onFocusItemEdit,
        onFocusItemDelete = viewModel::onFocusItemDelete,
        swapFocusItems = viewModel::swapFocusItems,
        swapTags = viewModel::swapTags,
        onTagEdit = viewModel::onTagEdit,
        onTagDelete = viewModel::onTagDelete
    )
}

@Composable
private fun Body(
    items: List<FocusBoardItemWithTags>,
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit,
    swapFocusItems: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    swapTags: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
) {
    MainBody {
        TopBar(tags, swapTags, onTagEdit, onTagDelete)

        Column(Modifier.padding(8.dp)) {


            val set = buildSet { addAll(tags.map { it.id }) }

            val toggle = remember(set) {
                mutableStateOf(set)
            }

            val items = remember(toggle.value, items) {
                derivedStateOf {
                    items.filter { it.tags.isEmpty() || it.tags.any { toggle.value.contains(it.id) } }
                }
            }

            HeaderWithTags(
                toggle = { toggle.value = toggle.value.toMutableSet().apply { toggle(it.id) } },
                tags = tags,
                selectedTags = toggle.value
            )

            FocusBoardItems(
                tags = tags,
                focusItems = items.value,
                swapFocusItems = swapFocusItems,
                onFocusItemDelete = onFocusItemDelete,
                onFocusItemEdit = onFocusItemEdit
            )
        }
    }
}

@Composable
private fun TopBar(
    tagsState: List<FocusBoardItemTag>,
    swapTags: (LazyListItemInfo, LazyListItemInfo) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, bottom = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.focus_board_title),
            fontWeight = FontWeight.Black, fontSize = 25.sp
        )

        val createEditTag = rememberDialog()


        DialogFocusBoardSettings(
            display = createEditTag,
            tags = tagsState,
            swapTags = swapTags,
            onTagEdit = onTagEdit,
            onTagDelete = onTagDelete
        )

        Row(
            modifier = Modifier
                .background(Colors.SuperLight, RoundedCornerShape(5.dp))
                .padding(4.dp)
                .clickable { createEditTag.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Tags", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FocusBoardItems(
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit,
    focusItems: List<FocusBoardItemWithTags>,
    swapFocusItems: (LazyListItemInfo, LazyListItemInfo) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        swapFocusItems(from, to)
    }

    if (focusItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp),
                imageVector = Icons.AutoMirrored.Outlined.FactCheck,
                contentDescription = "Focus item"
            )

            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = stringResource(R.string.focus_board_no_focus_items),
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )

            Text(
                text = "Add things you want to focus on that \ndon't require tracking.",
                textAlign = TextAlign.Center
            )

        }
    } else {
        Surface(
            modifier = Modifier.padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(focusItems, { it.item.id }) { item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.item.id
                    ) {
                        Box(Modifier.longPressDraggableHandle()) {
                            FocusBoardItem(
                                item,
                                tags,
                                onFocusItemEdit,
                                onFocusItemDelete,
                                if (it) Color.LightGray else item.getMainTag()?.getUIColor()
                                    ?: Colors.SuperLight
                            )
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun HeaderWithTags(
    toggle: (FocusBoardItemTag) -> Unit,
    tags: List<FocusBoardItemTag>,
    selectedTags: Set<Long>,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags, { item -> item.id }) { item ->
            FocusItemTag(
                onClick = { toggle(item) },
                isSelected = selectedTags.contains(item.id),
                name = item.name,
                color = item.color.toColor(),
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}

@Composable
fun FocusItemTag(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    name: String,
    color: Color,
    textModifier: Modifier = Modifier,
    iconAfter: ImageVector? = null,
    iconStart: ImageVector? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp,
        color = color,
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconStart != null) {
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    imageVector = iconStart,
                    contentDescription = ""
                )
            }

            Text(
                modifier = textModifier.padding(8.dp),
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (iconAfter != null) {
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    imageVector = iconAfter,
                    contentDescription = ""
                )
            }

        }


    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FocusBoardItem(
    item: FocusBoardItemWithTags,
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit,
    color: Color = item.getMainTag()?.getUIColor() ?: Colors.SuperLight,
) {

    var edit by remember { mutableStateOf(false) }

    if (edit) {
        BottomSheetEditFocusItem(
            onDismissRequest = {
                edit = false
            },
            isEdit = true,
            item = item,
            tags = tags,
            onFocusItemEdit = onFocusItemEdit,
            onFocusItemDelete = onFocusItemDelete
        )

    }

    Surface(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                edit = true
            },

        color = color,
        shadowElevation = 1.dp
    ) {
        Column(
            Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (item.item.title.isNotEmpty()) {
                Text(
                    item.item.title,
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                )
            }

            if (item.item.content.isNotEmpty()) {
                Text(
                    text = item.item.content,
                    style = TextStyle.Default.copy(
                        fontSize = 15.sp
                    ),
                )
            }

            if (item.tags.size > 1) {
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.tags.drop(1).forEach {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 1.dp,
                            color = it.getUIColor(),
                        ) {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = it.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

