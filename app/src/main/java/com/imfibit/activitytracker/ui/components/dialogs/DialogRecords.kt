package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.activityTables
import com.imfibit.activitytracker.core.extensions.navigate
import com.imfibit.activitytracker.core.invalidationStateFlow
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject


@Composable
fun DialogRecords(nav: NavHostController) {
    val vm = hiltViewModel<DayRecordsVM>()

    //https://issuetracker.google.com/issues/221643630#comment8
    Dialog(
        onDismissRequest = { nav.popBackStack() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {

        Surface(
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.width(320.dp)
        ){
            Column() {

                DialogBaseHeader(title = stringResource( R.string.dialog_records_title))

                val data by vm.data.collectAsState()

                if (data.isEmpty()) {
                    Text(text = stringResource(id = R.string.no_records), fontWeight= FontWeight.W600, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(0.dp, 200.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)

                    ){
                        items(data){
                            val record = it.record

                            Record(activity = it.activity, record = it.record, onCLick = {
                                nav.navigate("dialog_edit_record/{record}", bundleOf("record" to record))
                            })
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

    val data = invalidationStateFlow(db, listOf(), *activityTables){
        val activity = rep.activityDAO.flowById(activityId).first()

        val from = date.atStartOfDay()
        val to = from.plusDays(1L)


        rep.getRecords(activity.id, activity.type, from, to).map {
            RecordWithActivity(activity, it)
        }
    }
}