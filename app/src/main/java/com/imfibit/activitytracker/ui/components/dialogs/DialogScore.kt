package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.EditableDatetime
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector
import java.time.LocalDateTime


@Preview
@Composable
fun DialogScore_Preview() = AppTheme {
    DialogScore(
        record = DevSeeder.getTrackedTaskScore(),
        onDismissRequest = { },
        onUpdate = { _, _ -> },
        onDelete = { }
    )
}

@Composable
fun DialogScore(
    record: TrackedActivityScore,
    onDismissRequest: () -> Unit,
    onUpdate: ((LocalDateTime, Long) -> Unit),
    onDelete: (() -> Unit)? = null,
) = DialogScore(
    record.id > 0,
    record.score,
    record.datetime_completed,
    onDismissRequest,
    onUpdate,
    onDelete
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogScore(
    allowDelete: Boolean,
    score: Long,
    datetime: LocalDateTime,
    onDismissRequest: () -> Unit,
    onUpdate: ((LocalDateTime, Long) -> Unit),
    onDelete: (() -> Unit)? = null,
) = BaseDialog(onDismissRequest = onDismissRequest) {

    var score by remember { mutableStateOf(score.toInt()) }
    var datetime by remember { mutableStateOf(datetime) }

    DialogBaseHeader(title = stringResource(id = if (allowDelete) R.string.dialo_score_title_edit else R.string.dialo_score_title_add))

    NumberSelector(
        label = stringResource(id = R.string.score),
        number = score,
        range = 1..999
    ) {
        score = it
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.height(15.dp),
            textAlign = TextAlign.Center,
            text = "time",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
        )

        EditableDatetime(
            datetime = datetime,
            onDatetimeEdit = {
                datetime = it
            }
        )
    }

    DialogButtons {
        if (onDelete != null) {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onDelete.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }
        }

        TextButton(onClick = { onDismissRequest() }) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(onClick = {
            onDismissRequest()
            onUpdate(datetime, score.toLong())
        }) {
            Text(text = stringResource(id = if (allowDelete) R.string.dialog_action_edit else R.string.dialog_action_add))
        }
    }
}



