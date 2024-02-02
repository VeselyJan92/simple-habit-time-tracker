package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.DatetimeEditor
import com.imfibit.activitytracker.ui.components.selectors.NumberSelector
import java.time.LocalDateTime


@Composable
fun DialogScore(
    record: TrackedActivityScore,
    onDismissRequest: () -> Unit,
    onUpdate: ((LocalDateTime, Long) -> Unit),
    onDelete: (() -> Unit)? = null
) = DialogScore(record.id > 0, record.score, record.datetime_completed, onDismissRequest, onUpdate, onDelete)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogScore(
    allowDelete: Boolean,
    score: Long,
    datetime: LocalDateTime,
    onDismissRequest: () -> Unit,
    onUpdate: ((LocalDateTime, Long) -> Unit),
    onDelete: (() -> Unit)? = null,
) = BaseDialogV2(onDismissRequest = onDismissRequest) {

    val score = remember { mutableStateOf(score.toInt()) }
    val datetime = remember { mutableStateOf(datetime) }

    DialogBaseHeader(title = stringResource(id = if (allowDelete) R.string.dialo_score_title_edit else R.string.dialo_score_title_add))

    NumberSelector(
        label = stringResource(id = R.string.score),
        number = score.value,
        range = 1..999
    ) {
        score.value = it
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

        Box(
            Modifier
                .height(30.dp)
                .background(Colors.ChipGray, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            DatetimeEditor(datetime = datetime)
        }
    }

    DialogButtons {
        if (onDelete != null) {
            TextButton(
                onClick = {
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
            onUpdate.invoke(datetime.value, score.value.toLong())
        }) {
            Text(text = stringResource(id = if (allowDelete) R.string.dialog_action_edit else R.string.dialog_action_add))
        }
    }
}



