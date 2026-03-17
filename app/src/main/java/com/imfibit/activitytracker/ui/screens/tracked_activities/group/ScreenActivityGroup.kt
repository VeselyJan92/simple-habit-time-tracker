package com.imfibit.activitytracker.ui.screens.tracked_activities.group

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.ActivityFab
import com.imfibit.activitytracker.ui.components.EmptyState
import com.imfibit.activitytracker.ui.components.topBar.BackEditableTopBar
import com.imfibit.activitytracker.ui.components.dialogs.ConfirmDialog
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivity
import com.imfibit.activitytracker.ui.screens.tracked_activities.activity_list.components.TrackedActivityRecentOverview
import com.imfibit.activitytracker.ui.viewmodels.RecordViewModel
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ScreenActivityGroup(
    groupId: Long,
) {
    val navigation = hiltViewModel<BackstackViewModel>()

    val vm = hiltViewModel<ActivityGroupViewModel, ActivityGroupViewModel.Factory> { factory ->
        factory.create(groupId)
    }

    val recordVM = hiltViewModel<RecordViewModel>()

    val activities by vm.activities.collectAsStateWithLifecycle()
    val group by vm.group.collectAsStateWithLifecycle()

    group?.let {
        ScreenActivityGroup(
            activities = activities,
            group = group,
            name = vm.groupName.value ?: "",
            onDelete = {
                navigation.popBackStack()
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
                    is TrackedActivityScore -> navigation.navigate(
                        Destinations.DialogEditRecord(
                             TrackedActivityScore(
                                activity_id = it.activity_id,
                                datetime_completed = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                score = 1
                            )
                        )
                    )

                    is TrackedActivityTime -> navigation.navigate(
                        Destinations.DialogEditRecord(
                            TrackedActivityTime(
                                activity_id = it.activity_id,
                                datetime_start = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                datetime_end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            )
                        )
                    )
                }


            },
            onNavigateToActivity = {
                navigation.navigate(Destinations.ScreenActivity(it.id))
            },
            onNavigateBack = {
                navigation.popBackStack()
            },
            onCreateActivity = vm::createNewActivity
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
    onCreateActivity: (String, TrackedActivity.Type) -> Unit,
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            var dialogDelete by remember { mutableStateOf(false) }

            if (dialogDelete){
                ConfirmDialog(
                    onDismissRequest = { dialogDelete = false },
                    title = stringResource(id = R.string.screen_group_delete_group).uppercase(),
                    onAction = { delete ->
                        dialogDelete = false

                        val tobeDeleted = group

                        if (delete && tobeDeleted != null) {
                            onDelete(tobeDeleted)
                        }
                    }
                )

            }

            BackEditableTopBar(
                onTextChanged = {
                    onNameChanged(it)
                },
                onNavigateBack = {
                    onNavigateBack()

                },
                name = name,
                onActionClick = {
                    dialogDelete = true
                },
                actionIcon = Icons.Default.Delete

            )
        },
        floatingActionButton = {
            ActivityFab(
                expanded = fabExpanded,
                onExpandedChanged = { fabExpanded = it },
                onOptionSelected = { type ->
                    onCreateActivity("\uD83C\uDFAF Name", type)
                }
            )
        },
        content = { paddingValues ->
            Box(Modifier.fillMaxSize()) {
                ScreenBody(
                    paddingValues = paddingValues,
                    activities = activities,
                    onMove = onMove,
                    onActionButtonClick = onActionButtonClick,
                    onAddRecord = onAddRecord,
                    onNavigateToActivity = onNavigateToActivity
                )

                if (fabExpanded) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { fabExpanded = false }
                    )
                }
            }
        },
        containerColor = AppTheme.colors.background,
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

    if (activities.isEmpty()) {
        EmptyState(
            modifier = Modifier.padding(paddingValues),
            icon = Icons.Outlined.AssignmentTurnedIn,
            title = stringResource(id = R.string.no_activities_to_display),
            description = stringResource(id = R.string.screen_onboarding_page_habits_text)
        )
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

            items(activities, key = { item -> item.activity.id }) { item ->
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
