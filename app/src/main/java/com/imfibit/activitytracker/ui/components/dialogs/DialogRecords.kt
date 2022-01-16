package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.invalidationFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogRecords( nav: NavHostController ) {
    val vm = hiltViewModel<DayRecordsVM>()

    Dialog(onDismissRequest = {}) {

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp)),
            elevation = 2.dp
        ){
            Column(modifier = Modifier) {

                DialogBaseHeader(title = stringResource( R.string.dialog_records_title))

                val data by vm.x.collectAsState(initial = listOf())

                LazyColumn(modifier = Modifier.height(250.dp), contentPadding = PaddingValues(8.dp)){
                    if (data.isNotEmpty()){
                        items(data){
                            Record(activity = it.activity, record = it.record)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }else{
                        item {
                            Text(text = stringResource(id = R.string.no_records), fontWeight= FontWeight.W600, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    }

                }


                DialogButtons {
                    TextButton(onClick = {nav.popBackStack()}) {
                        Text(text = stringResource(id = R.string.dialog_records_btn_okey))
                    }
                }

            }
        }
    }
}


@HiltViewModel
class DayRecordsVM @Inject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val activityId: Long = savedStateHandle["activity_id"] ?: throw IllegalArgumentException()
    val date: LocalDate = LocalDate.parse(savedStateHandle["date"])?: throw IllegalArgumentException()

    val records = MutableLiveData<List<RecordWithActivity>>()


    val x = invalidationFlow(db){
        val activity = rep.activityDAO.flowById(activityId).first()

        val from = date.atStartOfDay()
        val to = from.plusDays(1L)


        rep.getRecords(activity.id, activity.type, from, to).map {
            RecordWithActivity(activity, it)
        }
    }
}

