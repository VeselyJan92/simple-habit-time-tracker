package com.imfibit.activitytracker.ui.screens.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.rememberReorderList
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITY
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogInputText
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview
import org.burnoutcrew.reorderable.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Delete
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.DialogAgree


@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun ScreenActivityGroup(nav: NavHostController, scaffoldState: ScaffoldState) {

    val vm = hiltViewModel<ActivityGroupViewModel>()

    val activities by vm.activities.collectAsState()
    val group by vm.group.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically) {

                TopBarBackButton(navHostController = nav)

                BasicTextField(
                    modifier = Modifier.weight(1f),
                    value = vm.groupName.value ?: "",
                    singleLine = true,
                    onValueChange = {vm.refreshName(it)},
                    textStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 25.sp)
                )

                val dialogDelete = remember {
                    mutableStateOf(false)
                }

                DialogAgree(
                    display = dialogDelete ,
                    title = stringResource(id = R.string.screen_group_delete_group).uppercase() ,
                    onAction = { delete ->
                        dialogDelete.value = false

                        val tobeDeleted = group

                        if(delete && tobeDeleted != null){
                            nav.popBackStack()
                            vm.delete(tobeDeleted)
                        }
                    }
                )

                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.Delete,
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(onClick = {
                            dialogDelete.value = true
                        })
                )


            }

        },
        content = {
            ScreenBody(nav, vm, group, activities)
        },
        backgroundColor = Colors.AppBackground,
        scaffoldState = scaffoldState
    )
}

@Composable
private fun ScreenBody(
    nav: NavHostController,
    vm: ActivityGroupViewModel,
    group: TrackerActivityGroup?,
    activities: List<TrackedActivityRecentOverview>,
) {
    Activities(
        nav = nav,
        activities = activities,
        onMove = { from, to -> vm.moveActivity(from, to) },
        onDragEnd = { from, to -> vm.onActivityDragEnd(from, to) }
    )

}

@Composable
private fun Activities(
    nav: NavHostController,
    activities: List<TrackedActivityRecentOverview>,
    onDragEnd: (from: Int, to: Int) -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
) {

    val activities = rememberReorderList(items = activities)

    val state = rememberReorderableLazyListState(
        onDragEnd = onDragEnd,
        onMove = {from, to -> onMove(from.index, to.index)}
    )

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state = state)
            .detectReorderAfterLongPress(state),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(activities.value, key = {item -> item.activity.id },) { item ->
            ReorderableItem(state, key = item.activity.id, defaultDraggingModifier = Modifier) { isDragging ->
                TrackedActivity(
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(SCREEN_ACTIVITY(it.id.toString())) },
                    isDragging = isDragging
                )
            }

        }

    }
}

