package com.imfibit.activitytracker.ui.screens.settings

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.SimpleTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScreenSetting(navControl: NavHostController) {

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            SimpleTopBar(
                title = stringResource(id = R.string.screen_settings_title),
                onBack = { navControl.popBackStack() }
            )
        },

        content = {
            LazyColumn(
                modifier = Modifier.padding(it),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                item {
                    AppSettings()
                }

                item {
                    BackupDatabase()
                }
            }
        },

        containerColor = Colors.AppBackground
    )
}

@Composable
fun AppSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current

        val notificationState = remember {
            mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
        }

        val startForResult =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                notificationState.value = it
            }

        val notificationLabel = if (notificationState.value)
            stringResource(id = R.string.screen_settings_application_group_notifications_enabled)
        else
            stringResource(id = R.string.screen_settings_application_group_notifications_disabled)

        SettingsGroup(stringResource(id = R.string.screen_settings_application_group)) {
            SettingsListItem(
                icon = Icons.Default.Notifications,
                title = stringResource(id = R.string.screen_settings_application_group_notifications),
                subtitle = notificationLabel,
                onClick = {
                    startForResult.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            )
        }
    }
}

@Composable
fun BackupDatabase() {
    SettingsGroup(stringResource(id = R.string.screen_settings_backup_section)) {

        val context = LocalContext.current
        val vm = hiltViewModel<ScreenSettingVM>()

        val unsupported = stringResource(id = R.string.screen_settings_backup_unsupported)

        val export =
            rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
                if (it == null)
                    Toast.makeText(context, unsupported, Toast.LENGTH_LONG).show()
                else
                    vm.exportDB(context, it)
            }

        val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null)
                Toast.makeText(context, unsupported, Toast.LENGTH_LONG).show()
            else
                vm.importDB(context, it)
        }

        SettingsListItem(
            icon = Icons.Default.FileDownload,
            title = stringResource(id = R.string.screen_settings_backup_label),
            subtitle = stringResource(id = R.string.screen_settings_backup_explain),
            onClick = {
                export.launch(
                    "activity_tracker_${
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }.db"
                )
            },
        )

        HorizontalDivider()

        SettingsListItem(
            icon = Icons.Default.FileUpload,
            title = stringResource(id = R.string.screen_settings_restore_label),
            subtitle = stringResource(id = R.string.screen_settings_restore_explain),
            onClick = {
                import.launch(arrayOf("application/octet-stream"))
            },
        )
    }


}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            content()
        }
    }

}


@Composable
fun SettingsListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null // Title can serve as content description here
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun HorizontalDivider() {
    HorizontalDivider(modifier = Modifier.padding(8.dp))
}

