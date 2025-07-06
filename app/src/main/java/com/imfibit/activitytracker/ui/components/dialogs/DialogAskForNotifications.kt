package com.imfibit.activitytracker.ui.components.dialogs

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.AppViewModel
import com.imfibit.activitytracker.ui.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Preview
@Composable
private fun DialogAskForNotificationsPreview() = AppTheme {
    DialogAskForNotifications(
        onDismissRequest = { },
        onGranted = { }
    )
}

@Composable
fun CheckNotificationPermission() {
    var askForNotifications by remember { mutableStateOf(false) }

    val appVM = hiltViewModel<AppViewModel>()

    if (askForNotifications){
        DialogAskForNotifications(
            onDismissRequest = { askForNotifications = false }
        ) {
            runBlocking {
                appVM.settings.setShouldShowNotificationsPopup(false)
            }
        }
    }

    LaunchedEffect(true) {
        if (appVM.settings.getShouldShowNotificationsPopup() != false) {
            delay(2000)
            askForNotifications = true
        }
    }
}

@Composable
fun DialogAskForNotifications(
    onDismissRequest: () -> Unit,
    onGranted: (Boolean) -> Unit,
) = BaseDialog(onDismissRequest = onDismissRequest) {

    val startForResult = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(), onGranted
    )

    DialogBaseHeader(title = stringResource(R.string.dialog_ask_for_notifications_title))

    Text(
        text = "We need notification permission:",
        modifier = Modifier.padding(8.dp)
    )

    Text(
        text = stringArrayResource(id = R.array.dialog_ask_for_notifications_list).joinToString("\n"),
        modifier = Modifier.padding(8.dp)
    )

    DialogButtons {

        TextButton(
            onClick = {
                onDismissRequest()
                onGranted(false)
            }
        ) {
            Text(text = "NO NOTIFICATIONS")
        }

        TextButton(
            onClick = {
                onDismissRequest()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    startForResult.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        ) {
            Text(text = "PERMIT")
        }
    }
}
