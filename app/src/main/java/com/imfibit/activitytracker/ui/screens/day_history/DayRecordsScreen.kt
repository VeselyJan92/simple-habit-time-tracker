
package com.imfibit.activitytracker.ui.screens.day_history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.SectionHeader
import com.imfibit.activitytracker.ui.components.TrackedActivityRecord
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScreenDayRecords() {

    val vm  = viewModel<DayRecordsVM>()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_record_history)) },
        content = {
            ScreenBody(vm, scaffoldState)
        },
        snackbarHost = {
            SnackbarHost(hostState = it)
        },
        backgroundColor = Colors . AppBackground,
        scaffoldState = scaffoldState
    )
}


@Composable
private fun ScreenBody(vm: DayRecordsVM, scaffoldState: ScaffoldState) {
    Column {

        Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                SectionHeader(vm.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))

            }
        }

        val items = vm.records.observeAsState(listOf())

        if (items.value.isEmpty()){
            Box(Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.no_records),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }else{
            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items( items.value.size){
                    val item = items.value[it]
                    TrackedActivityRecord(item.activity,  item.record, scaffoldState)
                }
            }
        }



    }

}


