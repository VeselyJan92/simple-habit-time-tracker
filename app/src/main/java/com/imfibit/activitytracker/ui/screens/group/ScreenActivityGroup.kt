package com.imfibit.activitytracker.ui.screens.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.extensions.navigate
import com.imfibit.activitytracker.core.extensions.rememberReorderList
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TopBarBackButton
import com.imfibit.activitytracker.ui.components.dialogs.DialogAgree
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivity
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDateTime


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ScreenActivityGroup(
    nav: NavHostController,
) {
    val vm = hiltViewModel<ActivityGroupViewModel>()
    val recordVM = hiltViewModel<RecordViewModel>()

    val activities by vm.activities.collectAsState()
    val group by vm.group.collectAsState()

    group?.let {
        ScreenActivityGroup(
            activities = activities,
            group = group,
            name = vm.groupName.value ?: "",
            onDelete = {
                nav.popBackStack()
                vm.delete(it)
            },
            onNameChanged = vm::refreshName,
            onMove = vm::onMoveActivity,
            onActionButtonClick = {
                recordVM.activityTriggered(it)
            },
            onAddRecord = {
                when (it) {
                    is TrackedActivityCompletion -> throw IllegalStateException("Completion not supported")
                    is TrackedActivityScore -> nav.navigate(
                        "dialog_edit_record/{record}",
                        bundleOf(
                            "record" to TrackedActivityScore(
                                activity_id = it.activity_id,
                                datetime_completed = LocalDateTime.now(),
                                score = 1
                            )
                        )
                    )

                    is TrackedActivityTime -> nav.navigate(
                        "dialog_edit_record/{record}",
                        bundleOf(
                            "record" to TrackedActivityTime(
                                activity_id = it.activity_id,
                                datetime_start = LocalDateTime.now(),
                                datetime_end = LocalDateTime.now()
                            )
                        )
                    )
                }


            },
            onNavigateToActivity = {
                nav.navigate(Destinations.ScreenActivity(it.id))
            },
            onNavigateBack = {
                nav.popBackStack()
            }
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ScreenActivityGroup(
    activities: List<TrackedActivityRecentOverview>,
    group: TrackerActivityGroup?,
    name: String,
    onDelete: (TrackerActivityGroup) -> Unit,
    onNameChanged: (String) -> Unit,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
    onActionButtonClick: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
    onNavigateToActivity: (TrackedActivity) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TopBarBackButton(onBack = onNavigateBack)

                BasicTextField(
                    modifier = Modifier.weight(1f),
                    value = name,
                    singleLine = true,
                    onValueChange = onNameChanged,
                    textStyle = TextStyle(fontWeight = FontWeight.Black, fontSize = 25.sp)
                )

                val dialogDelete = remember {
                    mutableStateOf(false)
                }

                DialogAgree(
                    display = dialogDelete,
                    title = stringResource(id = R.string.screen_group_delete_group).uppercase(),
                    onAction = { delete ->
                        dialogDelete.value = false

                        val tobeDeleted = group

                        if (delete && tobeDeleted != null) {
                            onDelete(tobeDeleted)
                        }
                    }
                )

                Icon(
                    contentDescription = null,
                    imageVector = Icons.Default.Delete,
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(
                            onClick = {
                                dialogDelete.value = true
                            }
                        )
                )
            }

        },
        content = { paddingValues ->
            ScreenBody(
                paddingValues = paddingValues,
                activities = activities,
                onMove = onMove,
                onActionButtonClick = onActionButtonClick,
                onAddRecord = onAddRecord,
                onNavigateToActivity = onNavigateToActivity
            )
        },
        containerColor = Colors.AppBackground,
    )
}

@Composable
private fun ScreenBody(
    paddingValues: PaddingValues,
    activities: List<TrackedActivityRecentOverview>,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
    onActionButtonClick: (TrackedActivity) -> Unit,
    onAddRecord: (TrackedActivityRecord) -> Unit,
    onNavigateToActivity: (TrackedActivity) -> Unit,
) {
    val activities = rememberReorderList(items = activities)

    if (activities.value.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp),
                imageVector = Icons.Outlined.AssignmentTurnedIn,
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "Add tracked habit",
                fontWeight = FontWeight.Bold, fontSize = 18.sp
            )
        }
    } else {
        val lazyListState = rememberLazyListState()
        val reorderableLazyListState =
            rememberReorderableLazyListState(lazyListState) { from, to ->
                onMove(from, to)
            }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {

            items(activities.value, key = { item -> item.activity.id }) { item ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = item.activity.id
                ) { isDragging ->
                    TrackedActivity(
                        item = item,
                        modifier = Modifier.longPressDraggableHandle(),
                        onNavigate = onNavigateToActivity,
                        isDragging = isDragging,
                        onActionButtonClick = onActionButtonClick,
                        onAddRecord = onAddRecord
                    )
                }
            }
        }
    }
}
