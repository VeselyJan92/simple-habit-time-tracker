package com.imfibit.activitytracker.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.SimpleTopBar

@Composable
fun ScreenSetting(navControl: NavHostController, scaffoldState: ScaffoldState) {



    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SimpleTopBar(navHostController = navControl, title =  stringResource(id = R.string.screen_settings_title))
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

        val unsupported = stringResource(id = R.string.screen_settings_backup_unsupported)

        val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
            if(it == null)
                Toast.makeText(context,unsupported , Toast.LENGTH_LONG).show()
            else
                vm.exportDB(context, it)
        }

        val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            if(it == null)
                Toast.makeText(context,unsupported , Toast.LENGTH_LONG).show()
            else
                vm.importDB(context, it)
        }

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                text = stringResource(id = R.string.screen_settings_backup_section),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            SettingsMenuItem(
                icon = { Icon(imageVector = Icons.Default.FileDownload, null) },
                title = stringResource(id = R.string.screen_settings_backup_label),
                subtitle = stringResource(id = R.string.screen_settings_backup_explain) ,
                onClick = {
                    export.launch(AppDatabase.DB_NAME)
                },
            )

            Divider()

            SettingsMenuItem(
                icon = { Icon(imageVector = Icons.Default.FileUpload, null) },
                title = stringResource(id = R.string.screen_settings_restore_label),
                subtitle =  stringResource(id = R.string.screen_settings_restore_explain),
                onClick = {
                    import.launch(arrayOf("application/octet-stream"))
                },
            )
        }
    }
}



@Composable
fun SettingsMenuItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
){

    Row( modifier = Modifier.clickable{ onClick()}) {
        Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center){
            icon()
        }

        Column() {
            Text(text = title, fontWeight = FontWeight.W600)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = subtitle)
        }

    }
}

@Composable
fun Divider(){
    Divider(modifier = Modifier.padding(8.dp))
}

