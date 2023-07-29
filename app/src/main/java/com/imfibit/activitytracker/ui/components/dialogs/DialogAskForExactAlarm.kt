package com.imfibit.activitytracker.ui.components.dialogs

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R

@Composable
inline fun DialogAskForExactAlarm(
    display: MutableState<Boolean> = mutableStateOf(true),
) = BaseDialog(display = display) {

    val context : Context = androidx.compose.ui.platform.LocalContext.current
    DialogBaseHeader(title = stringResource(R.string.dialog_exact_alarm_title))

    Text(
        text = stringResource(R.string.dialog_exact_alarm_body),
        modifier = Modifier.padding(8.dp)
    )

    DialogButtons {

        TextButton(onClick = {display.value = false} ) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                display.value = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}
