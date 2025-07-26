package com.imfibit.activitytracker.ui.components.dialogs

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager.PERMISSION_GRANTED
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.checkSelfPermission
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.AppTheme
import kotlinx.coroutines.delay

@Preview
@Composable
private fun DialogAskForNotificationsPreview() = AppTheme {
    DialogAskForNotifications(
        onDismissRequest = { },
    )
}

@Composable
fun CheckNotificationPermission(
    shouldAsk: Boolean,
    onDoNotAsk: () -> Unit,
    onGranted: (Boolean) -> Unit = { },
) {
    val context = LocalContext.current

    val isGranted = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED
        } else {
            onGranted(true)
            true
        }
    }

    var askForNotifications by remember { mutableStateOf(false) }

    if (askForNotifications) {
        DialogAskForNotifications(
            onDismissRequest = {
                askForNotifications = false
                onDoNotAsk()
            },
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        askForNotifications = !isGranted && shouldAsk
    }
}

@Composable
fun DialogAskForNotifications(
    onDismissRequest: () -> Unit,
    onGranted: (Boolean) -> Unit = { },
) = BaseDialog(onDismissRequest = onDismissRequest) {
    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onGranted
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
            }
        ) {
            Text(text = stringResource(R.string.dialog_ask_for_notifications_disallow))
        }

        TextButton(
            onClick = {
                onDismissRequest()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    startForResult.launch(POST_NOTIFICATIONS)
                }
            }
        ) {
            Text(text = stringResource(R.string.dialog_ask_for_notifications_allow))
        }
    }
}
