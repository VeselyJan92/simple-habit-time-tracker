package com.imfibit.activitytracker.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alorma.settings.composables.SettingsMenuLink
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar

@Composable
fun ScreenSetting(navControl: NavHostController) {
    Scaffold(
        topBar = {
            TrackerTopAppBar(stringResource(id = R.string.screen_settings_title))
        },

        content = {
            ScreenBody()
        },

        backgroundColor = Colors.AppBackground
    )

}

@Composable
private fun ScreenBody(){
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp
    ) {

        val context = LocalContext.current
        val vm = hiltViewModel<ScreenSettingVM>()

        val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
            vm.exportDB(context, it!!)
        }

        val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            vm.importDB(context, it!!)
        }


        Column() {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.screen_settings_backup_section),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            SettingsMenuLink(
                icon = { Icon(imageVector = Icons.Default.FileDownload, null) },
                title = { Text(text = stringResource(id = R.string.screen_settings_backup_label)) },
                subtitle = { Text(text = stringResource(id = R.string.screen_settings_backup_explain)) },
                onClick = {
                    export.launch(AppDatabase.DB_NAME)
                },
            )

            SettingsMenuLink(
                icon = { Icon(imageVector = Icons.Default.FileUpload, null) },
                title = { Text(text = stringResource(id = R.string.screen_settings_restore_label)) },
                subtitle = { Text(text = stringResource(id = R.string.screen_settings_restore_explain)) },
                onClick = {
                    import.launch(arrayOf("application/octet-stream"))
                },
            )
        }
    }
}