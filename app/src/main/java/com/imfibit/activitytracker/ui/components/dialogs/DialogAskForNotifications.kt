package com.imfibit.activitytracker.ui.components.dialogs

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.core.services.NotificationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@Composable
fun CheckNotificationPermission() {
    val context = LocalContext.current

    val askForNotifications = remember {
        mutableStateOf(false)
    }

    DialogAskForNotifications(askForNotifications){
        runBlocking {
            context.dataStore.edit { settings -> settings[PreferencesKeys.ASK_FOR_NOTIFICATION] = false }
        }

        askForNotifications.value = false
    }

    LaunchedEffect(true ) {
        if (NotificationService(context).shouldAskForNotification()){

            delay(2000)
            askForNotifications.value = true
        }
    }
}

@Composable
fun DialogAskForNotifications(
    display: MutableState<Boolean> = mutableStateOf(true),
    onGranted: (Boolean) -> Unit
) = BaseDialog(display = display) {

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(), onGranted)

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
                onGranted(false)
            }
        ) {
            Text(text = "NO NOTIFICATIONS")
        }

        TextButton(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    startForResult.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        ) {
            Text(text = "PERMIT")
        }
    }
}