package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberTestBottomSheetState(): SheetState {
    val density = LocalDensity.current

    return remember {
        SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Expanded,
            density = density
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    state: SheetState,
    content: @Composable ColumnScope.(
        onDismissRequest: (action: (() -> Unit)?) -> Unit,
    ) -> Unit,
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
            .displayCutoutPadding(),
        containerColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        content = {
            content(animateToDismiss)
        },
    )
}
