package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTestBottomSheetState(): SheetState {
    return rememberStandardBottomSheetState(
        skipHiddenState = true,
        initialValue = SheetValue.Expanded
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppBottomSheetState(): SheetState {
    return rememberModalBottomSheetState(skipPartiallyExpanded = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    state: SheetState,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable ColumnScope.(onDismissRequest: (action: (() -> Unit)?) -> Unit, ) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val animateToDismiss: (actionAfterClose: (() -> Unit)?) -> Unit = { actionAfterClose ->
        scope
            .launch { state.hide() }
            .invokeOnCompletion {
                if (!state.isVisible) {
                    onDismissRequest()
                    actionAfterClose?.invoke()
                }
            }
    }

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .imePadding()
            .displayCutoutPadding()
            .statusBarsPadding()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        content = {
            Column(
                modifier = Modifier.padding(paddingValues).navigationBarsPadding()
            ) {
                content(animateToDismiss)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    state: SheetState,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable ColumnScope.(
        onDismissRequest: (action: (() -> Unit)?) -> Unit,
    ) -> Unit,
) {
    BaseBottomSheet(
        modifier = modifier,
        state = state,
        onDismissRequest = onDismissRequest,
        paddingValues = paddingValues,
        content = { onDismissRequest ->
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                content(onDismissRequest)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyScrollBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    state: SheetState,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: LazyListScope.(
        onDismissRequest: (action: (() -> Unit)?) -> Unit,
    ) -> Unit,
) {
    BaseBottomSheet(
        modifier = modifier,
        state = state,
        onDismissRequest = onDismissRequest,
        paddingValues = PaddingValues(0.dp),
        content = { onDismissRequest ->
            LazyColumn(
                contentPadding = paddingValues
            ) {
                content(onDismissRequest)
            }
        }
    )
}
