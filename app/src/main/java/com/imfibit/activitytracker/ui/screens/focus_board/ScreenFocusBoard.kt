package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.toggle
import com.imfibit.activitytracker.core.toColor
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.MainBody
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.rememberDialog
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

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
private fun Preview() {
    val context = LocalContext.current

    Body(
        navControl = NavHostController(context),
        items = remember { mutableStateListOf(
            FocusBoardItemWithTags(DevSeeder.getFocusBoardItem(), listOf(DevSeeder.getFocusBoardItemTag()))
        ) },
        tags = remember {mutableStateListOf() },
        onFocusItemEdit = {},
        onFocusItemDelete = {},
        reorderFocusItems = {},
        swapFocusItems = {_, _ -> },
        reorderTags = { },
        swapTags = {_, _ -> },
        onTagEdit = {},
        onTagDelete = {}
    )
}



@Composable
fun ScreenFocusBoard(navControl: NavHostController) {

    val viewModel = hiltViewModel<FocusBoardViewModel>()

    Body(
        navControl = navControl,
        items = viewModel.focusItems,
        tags = viewModel.tags,
        onFocusItemEdit = viewModel::onFocusItemEdit,
        onFocusItemDelete = viewModel::onFocusItemDelete,
        reorderFocusItems = viewModel::reorderFocusItems,
        swapFocusItems = viewModel::swapFocusItems,
        reorderTags = viewModel::reorderTags,
        swapTags = viewModel::swapTags,
        onTagEdit = viewModel::onTagEdit,
        onTagDelete = viewModel::onTagDelete
    )
}

@Composable
private fun Body(
    navControl: NavHostController,
    items: SnapshotStateList<FocusBoardItemWithTags>,
    tags: SnapshotStateList<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit,
    reorderFocusItems: () -> Unit,
    swapFocusItems: (ItemPosition, ItemPosition) -> Unit,
    reorderTags: () -> Unit,
    swapTags: (ItemPosition, ItemPosition) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit
) {
    MainBody {
        TopBar(tags, reorderTags, swapTags, onTagEdit, onTagDelete)

        Column(Modifier.padding(8.dp)) {


            val set = buildSet { addAll(tags.map{it.id})}

            val toggle = remember(set){
                mutableStateOf(set)
            }

            val items = remember(toggle.value) {
                derivedStateOf {
                    items.filter { it.tags.isEmpty() || it.tags.any { toggle.value.contains(it.id) } }
                }
            }

            HeaderWithTags(
                tagsState = tags,
                reorderTags = reorderTags,
                swapTags = swapTags,
                onTagEdit = onTagEdit,
                onTagDelete = onTagDelete,
                selectedTags = toggle.value,
                toggle = { toggle.value = toggle.value.toMutableSet().apply { toggle(it.id) } }
            )

            FocusBoardItems(
                tags = tags,
                focusItems = items.value,
                reorderFocusItems = reorderFocusItems,
                swapFocusItems = swapFocusItems,
                onFocusItemDelete = onFocusItemDelete,
                onFocusItemEdit = onFocusItemEdit
            )
        }
    }
}

@Composable
private fun TopBar(
    tagsState: SnapshotStateList<FocusBoardItemTag>,
    reorderTags: () -> Unit,
    swapTags: (ItemPosition, ItemPosition) -> Unit,
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
            reorderTags = reorderTags,
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
            Icon(imageVector = Icons.Default.Edit, contentDescription = null )

            Spacer(modifier = Modifier.width(8.dp))
            
            Text(text = "Tags", fontSize = 18.sp, fontWeight = FontWeight.Medium )
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FocusBoardItems(
    tags : SnapshotStateList<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags)->Unit,
    onFocusItemDelete: (FocusBoardItemWithTags)->Unit,
    focusItems: List<FocusBoardItemWithTags>,
    reorderFocusItems: ()->Unit,
    swapFocusItems: (ItemPosition, ItemPosition)->Unit)
{

    val reorderState = rememberReorderableLazyListState(
        onDragEnd = { from, to -> reorderFocusItems()},
        onMove = swapFocusItems
    )

    if(focusItems.isEmpty()){
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .height(100.dp), contentAlignment = Alignment.Center){
            Text(text = stringResource(R.string.focus_board_no_focus_items), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }else{
        Surface(
            modifier = Modifier.padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            elevation = 2.dp
        ) {
            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .reorderable(state = reorderState)
                    .detectReorderAfterLongPress(reorderState),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {

                items(focusItems, {it.item.id} ){ item ->
                    ReorderableItem(
                        state = reorderState,
                        key = item.item.id,
                        modifier = Modifier,
                        defaultDraggingModifier = if (reorderState.draggingItemKey == null) Modifier else Modifier.animateItemPlacement()
                    ) {
                        FocusBoardItem(item, tags, onFocusItemEdit, onFocusItemDelete, if (it) Color.LightGray  else item.getMainTag()?.getUIColor() ?: Colors.SuperLight)
                    }
                }
            }
        }

    }
}


@Composable
fun HeaderWithTags(
    toggle: (FocusBoardItemTag)-> Unit,
    tagsState: SnapshotStateList<FocusBoardItemTag>,
    selectedTags: Set<Long>,
    reorderTags: () -> Unit,
    swapTags: (ItemPosition, ItemPosition) -> Unit,
    onTagEdit: (FocusBoardItemTag) -> Unit,
    onTagDelete: (FocusBoardItemTag) -> Unit,
) {



    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){

        items(tagsState, {item -> item.id }){ item ->

            FocusItemTag(
                onClick =  { toggle(item) },
                isSelected = selectedTags.contains(item.id),
                name = item.name,
                color = item.color.toColor(),
                tagModifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}

@Composable
fun FocusItemTag(
    onClick: ()->Unit,
    isSelected: Boolean = false,
    name: String,
    color: Color,
    textModifier: Modifier = Modifier,
    tagModifier: Modifier = Modifier,
    iconAfter: ImageVector? = null,
    iconStart: ImageVector? = null
) {
    Surface(
        modifier = tagModifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = 1.dp,
        color = color,
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null
    ) {

        Row(
            modifier = tagModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconStart != null){
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

            if (iconAfter != null){
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    imageVector = iconAfter,
                    contentDescription = ""
                )
            }

        }


    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FocusBoardItem(
    item: FocusBoardItemWithTags,
    tags: List<FocusBoardItemTag>,
    onFocusItemEdit: (FocusBoardItemWithTags) -> Unit,
    onFocusItemDelete: (FocusBoardItemWithTags) -> Unit,
    color: Color = item.getMainTag()?.getUIColor() ?: Colors.SuperLight
) {

    val edit = remember {
        mutableStateOf(false)
    }

    DialogEditFocusItem(display = edit, isEdit = true, item = item, tags = tags, onFocusItemEdit = onFocusItemEdit, onFocusItemDelete = onFocusItemDelete )

    Surface(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                edit.value = true
            },

        color = color,
        elevation = 1.dp
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
                Text(item.item.content)
            }

            if (item.tags.size > 1){
                FlowRow(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.tags.drop(1).forEach {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            elevation = 1.dp,
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

