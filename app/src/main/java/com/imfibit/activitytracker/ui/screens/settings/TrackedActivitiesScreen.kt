package com.imfibit.activitytracker.ui.screens.settings

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.selection.AmbientSelectionRegistrar
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.registerForActivityResult
import com.imfibit.activitytracker.ui.AppBottomNavigation
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TextButton
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar

@ExperimentalLayout
@Composable
fun ScreenSettings(
    navController: NavHostController,
){
    val vm = viewModel<SettingsViewModel>()

    Scaffold(
        topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)) },
        bodyContent = { Body(vm) },
        bottomBar = { AppBottomNavigation(navController) },
        backgroundColor = Colors.AppBackground
    )
}

@Composable
private fun Body(vm: SettingsViewModel){
    ImportExportDBSetting(vm = vm)
}


@Composable
internal fun SettingTwoRowItem(title:String, text: String, action: String, onclick: ()->Unit){
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W500))

            TextButton(text = action , onclick = onclick)
        }

        Row {
            Text(text = text, style = TextStyle(color = Color.Black.copy(alpha = 0.6f )))
        }
    }
}

@Composable
internal fun SettingsHeader(title: String){
    Text(
        modifier = Modifier.padding(8.dp),
        text = title,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.W500)
    )
}

@Composable
internal fun SettingsDivider(){
    Divider(modifier = Modifier.padding(horizontal = 8.dp))
}
