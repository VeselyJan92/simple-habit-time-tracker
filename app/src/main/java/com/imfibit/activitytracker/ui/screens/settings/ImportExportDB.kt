package com.imfibit.activitytracker.ui.screens.settings

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.core.registerForActivityResult

@Composable
fun ImportExportDBSetting(vm: SettingsViewModel){
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {
        Column {
            SettingsHeader("Backup")

            SettingTwoRowItem(
                title = "Backup data",
                text = "For safety of your data sometimes back up the database ",
                action = "Export",
                onclick = {
                    vm.export()
                }
            )

            SettingsDivider()

            val launcher = registerForActivityResult(ActivityResultContracts.OpenDocument()){
               vm.import(it)
            }

            SettingTwoRowItem(
                title = "Restore data",
                text = "For safety of your data sometimes back up the database ",
                action = "Import",
                onclick = {
                    launcher.launch(arrayOf("application/octet-stream", "*"))
                }
            )
        }
    }
}