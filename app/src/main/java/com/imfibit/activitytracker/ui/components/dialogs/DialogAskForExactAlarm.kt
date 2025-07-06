package com.imfibit.activitytracker.ui.components.dialogs

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R

@Preview
@Composable
fun DialogAskForExactAlarm_Preview() {
    DialogAskForExactAlarm(
        onDismissRequest = { }
    )
}

@Composable
fun DialogAskForExactAlarm(
    onDismissRequest: () -> Unit
) = BaseDialog(onDismissRequest = onDismissRequest) {

    val context = LocalContext.current

    DialogBaseHeader(title = stringResource(R.string.dialog_exact_alarm_title))

    Text(
        text = stringResource(R.string.dialog_exact_alarm_body),
        modifier = Modifier.padding(8.dp)
    )

    DialogButtons {

        TextButton(
            onClick = {
                onDismissRequest()
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            onClick = {
                onDismissRequest()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}
