package com.imfibit.activitytracker.ui.screens.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
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
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.components.Colors


@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun ScreenActivityGroup(nav: NavHostController, scaffoldState: ScaffoldState) {

    val vm = hiltViewModel<ActivityGroupViewModel>()



    Scaffold(
        topBar = {
            SimpleTopBar(navHostController = nav, title = stringResource(id = R.string.screen_group_title))
        },
        content = {
            val activities by vm.activities.collectAsState()
            val group by vm.group.collectAsState()
            val groups by vm.groups.collectAsState()

            ScreenBody(nav, vm, group, activities, groups)
        },

        bottomBar = {

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
    groups: List<TrackerActivityGroup>,
) {
    Column(

    ) {
        Header(nav, vm, group, groups)

        Activities(
            nav = nav,
            activities = activities,
            onMove = { from, to -> vm.moveActivity(from, to) },
            onDragEnd = { from, to -> vm.onActivityDragEnd(from, to) }
        )
    }

}

@Composable
private fun Header(
    nav: NavHostController,
    vm: ActivityGroupViewModel,
    group: TrackerActivityGroup?,
    groups: List<TrackerActivityGroup>,
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        val editName = remember { mutableStateOf(false) }

        DialogInputText(
            display = editName,
            text = group?.name ?: "",
            title = stringResource(id = R.string.screen_group_edit_name)
        ) {
            vm.update(group!!.copy(name = it))
            editName.value = false
        }

        val reorderGroups = remember { mutableStateOf(false) }

        DialogReorderGroups(
            display = reorderGroups,
            groups = groups ,
            onDragEnd = { from: Int, to: Int -> vm.onGroupDragEnd(from, to)},
            onMove = {from: Int, to: Int -> vm.moveGroup(from, to) }
        )

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            TextBox(
                text = group?.name ?: "",
                modifier = Modifier.fillMaxWidth()
            ) {
                editName.value = true
            }

            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconTextButton(
                    Icons.Default.Reorder,
                    stringResource(id = R.string.screen_group_reorder)
                ) {
                    reorderGroups.value = true
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconTextButton(
                    Icons.Default.Palette,
                    stringResource(id = R.string.screen_group_color)
                ) {

                }
            }


        }


    }
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
            .detectReorderAfterLongPress(state)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(activities.value, key = {item -> item.activity.id },) { item ->
            ReorderableItem(state, key = item.activity.id, defaultDraggingModifier = Modifier) { isDragging ->
                TrackedActivity(
                    item = item,
                    modifier = Modifier,
                    onNavigate = { nav.navigate(SCREEN_ACTIVITY(it.id.toString())) }
                )
            }

        }

    }
}


@Composable
fun DialogReorderGroups(
    display: MutableState<Boolean>,
    groups: List<TrackerActivityGroup>,
    onDragEnd: (from: Int, to: Int) -> Unit,
    onMove: (from: Int, to: Int) -> Unit
) = BaseDialog(display = display) {

    DialogBaseHeader(title = stringResource(R.string.dialog_preset_timers_title))

    val state = rememberReorderableLazyListState(
        onDragEnd = onDragEnd,
        onMove = {from, to -> onMove(from.index, to.index)}
    )

    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 8.dp)
            .reorderable(state = state,)
            .detectReorderAfterLongPress(state)
    ) {

        items(groups, {it.id}) { item ->
            ReorderableItem(state, key = item.id, defaultDraggingModifier = Modifier) { isDragging ->
                Row(
                    modifier = Modifier

                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray, RoundedCornerShape(30))
                        .padding(4.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .background(Colors.ChipGray, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = item.name,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                }
            }


        }

    }
}

