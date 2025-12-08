package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.activityTables
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.AppDestination
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.Destinations
import com.imfibit.activitytracker.ui.components.Colors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@Preview
@Composable
fun DialogRecords_PreviewSomeItems() = AppTheme {
    DialogRecords(
        onDismissRequest = {},
        data = listOf(
            RecordWithActivity(
                DevSeeder.getTrackedActivityTime(),
                DevSeeder.getTrackedTaskSession()
            )
        ),
        onNavigate = {}
    )
}

@Preview
@Composable
fun DialogRecords_PreviewNoItems() = AppTheme {
    DialogRecords(
        onDismissRequest = {},
        data = listOf(),
        onNavigate = {}
    )
}


@Composable
fun DialogRecords(
    navigate: (AppDestination) -> Unit,
    popBack: () -> Unit,
    activityId: Long,
    date: LocalDate
) {
    val vm = hiltViewModel<DayRecordsVM, DayRecordsVM.Factory> { factory ->
        factory.create(activityId, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    val data by vm.data.collectAsState()

    DialogRecords(
        onDismissRequest = { popBack() },
        data = data,
        onNavigate = {
            navigate(
                Destinations.DialogEditRecord(it)
            )
        }
    )
}

@Composable
fun DialogRecords(
    onDismissRequest: () -> Unit,
    data: List<RecordWithActivity>,
    onNavigate: (TrackedActivityRecord) -> Unit,
) = BaseDialog(
    paddingValues = PaddingValues(top = 16.dp, bottom = 16.dp),
    onDismissRequest = onDismissRequest,
) {

    DialogBaseHeader(title = stringResource(R.string.dialog_records_title), modifier = Modifier.padding(horizontal = 16.dp))

    if (data.isEmpty()) {
        Text(
            text = stringResource(id = R.string.no_records),
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(0.dp, 200.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            items(data) {
                Record(
                    activity = it.activity,
                    record = it.record,
                    onCLick = {
                        onNavigate(it)
                    }
                )
            }
        }
    }

    DialogButtons(modifier = Modifier.padding(horizontal = 16.dp)) {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.dialog_records_btn_okey))
        }
    }
}

@Composable
fun Record(
    activity: TrackedActivity,
    record: TrackedActivityRecord,
    onCLick: (TrackedActivityRecord) -> Unit = {},
) {
    Surface(
        shadowElevation = 2.dp,
        modifier = Modifier
            .testTag(TestTag.ACTIVITY_RECORD)
            .clickable(onClick = {
                onCLick(record)
            }),
        shape = RoundedCornerShape(8.dp),
        color = Colors.SuperLight
    ) {
        val time = with(AnnotatedString.Builder()) {
            append(stringResource(id = R.string.time) + ": ")

            when (record) {
                is TrackedActivityCompletion -> {
                    append(
                        record.datetime_completed.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                }

                is TrackedActivityScore -> {
                    append(
                        record.datetime_completed.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                }

                is TrackedActivityTime -> {
                    append(
                        record.datetime_start.format(
                            DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                            )
                        )
                    )
                    append(" - ")
                    append(record.datetime_end.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
                }
            }

            toAnnotatedString()
        }

        val metric = activity.type.getLabel(record.metric).value()


        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time,
                modifier = Modifier.weight(1f),
                style = TextStyle.Default.copy(color = Color.Black.copy(alpha = 0.6f)),
            )


            Box(
                modifier = Modifier
                    .size(60.dp, 25.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = metric,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }

        }
    }
}

@HiltViewModel(assistedFactory = DayRecordsVM.Factory::class)
class DayRecordsVM @dagger.assisted.AssistedInject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryTrackedActivity,
    @dagger.assisted.Assisted("activityId") private val activityId: Long,
    @dagger.assisted.Assisted("date") private val date: String,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(
            @dagger.assisted.Assisted("activityId") activityId: Long,
            @dagger.assisted.Assisted("date") date: String
        ): DayRecordsVM
    }

    val data = invalidationStateFlow(db, listOf(), *activityTables) {
        val activity = rep.activityDAO.flowById(activityId).first()

        val from = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        val to = from.plusDays(1L)


        rep.getRecords(activity.id, activity.type, from, to).map {
            RecordWithActivity(activity, it)
        }
    }
}
