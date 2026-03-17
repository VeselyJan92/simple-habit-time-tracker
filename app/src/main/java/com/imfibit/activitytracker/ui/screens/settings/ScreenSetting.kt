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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.navigation.BackstackViewModel
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.topBar.SimpleBackTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScreenSetting() {
    val navigation = hiltViewModel<BackstackViewModel>()
    val vm = hiltViewModel<ScreenSettingVM>()
    val context = LocalContext.current

    val notificationState = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            } else {
                true // Notifications are always enabled on older devices
            }
        )
    }

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        notificationState.value = it
    }

    val unsupported = stringResource(id = R.string.screen_settings_backup_unsupported)

    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
        if (it == null) Toast.makeText(context, unsupported, Toast.LENGTH_LONG).show()
        else vm.exportDB(context, it)
    }

    val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it == null) Toast.makeText(context, unsupported, Toast.LENGTH_LONG).show()
        else vm.importDB(context, it)
    }

    ScreenSettingContent(
        notificationsEnabled = notificationState.value,
        onRequestNotificationPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startForResult.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onExportDatabase = {
            export.launch(
                "activity_tracker_${
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                }.db"
            )
        },
        onImportDatabase = {
            import.launch(arrayOf("application/octet-stream"))
        },
        onBack = { navigation.popBackStack() }
    )
}

@Composable
fun ScreenSettingContent(
    notificationsEnabled: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onExportDatabase: () -> Unit,
    onImportDatabase: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SimpleBackTopBar(
                title = stringResource(id = R.string.screen_settings_title),
                onBack = onBack
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    item {
                        AppSettings(
                            notificationsEnabled = notificationsEnabled,
                            onRequestNotificationPermission = onRequestNotificationPermission
                        )
                    }
                }

                item {
                    BackupDatabase(
                        onExportDatabase = onExportDatabase,
                        onImportDatabase = onImportDatabase
                    )
                }
            }
        },
        containerColor = AppTheme.colors.lightBackground // Clean frosted glass background
    )
}

@Composable
private fun AppSettings(
    notificationsEnabled: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    val notificationLabel = if (notificationsEnabled)
        stringResource(id = R.string.screen_settings_application_group_notifications_enabled)
    else
        stringResource(id = R.string.screen_settings_application_group_notifications_disabled)

    SettingsGroup(stringResource(id = R.string.screen_settings_application_group)) {
        SettingsListItem(
            icon = Icons.Default.Notifications,
            title = stringResource(id = R.string.screen_settings_application_group_notifications),
            subtitle = notificationLabel,
            onClick = onRequestNotificationPermission
        )
    }
}

@Composable
private fun BackupDatabase(
    onExportDatabase: () -> Unit,
    onImportDatabase: () -> Unit
) {
    SettingsGroup(stringResource(id = R.string.screen_settings_backup_section)) {
        SettingsListItem(
            icon = Icons.Default.FileDownload,
            title = stringResource(id = R.string.screen_settings_backup_label),
            subtitle = stringResource(id = R.string.screen_settings_backup_explain),
            onClick = onExportDatabase
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = AppTheme.colors.divider.copy(alpha = 0.5f)
        )

        SettingsListItem(
            icon = Icons.Default.FileUpload,
            title = stringResource(id = R.string.screen_settings_restore_label),
            subtitle = stringResource(id = R.string.screen_settings_restore_explain),
            onClick = onImportDatabase
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onSurfaceVariant // Muted grey header
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppTheme.colors.surfaceContainerLow.copy(alpha = 0.9f), // Translucent frosted glass effect
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = AppTheme.colors.iconBackground.copy(alpha = 0.7f), // Soft grey icon background
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppTheme.colors.primary, // Deep purple accent
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppTheme.colors.onSurface // Strong dark grey
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = AppTheme.colors.onSurfaceVariant // Muted subtext
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenSettingPreview() = AppTheme {
    ScreenSettingContent(
        notificationsEnabled = true,
        onRequestNotificationPermission = {},
        onExportDatabase = {},
        onImportDatabase = {},
        onBack = {}
    )
}