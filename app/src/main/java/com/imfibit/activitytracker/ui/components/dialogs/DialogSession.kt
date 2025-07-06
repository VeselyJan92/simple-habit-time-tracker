package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.EditableDatetime
import java.time.Duration
import java.time.LocalDateTime

@Preview
@Composable
fun DialogSession_Preview() = AppTheme {
    DialogSession(
        allowDelete = true,
        from = LocalDateTime.now(),
        to = LocalDateTime.now().plusMinutes(60),
        onUpdate = { _, _ -> },
        onDismissRequest = { },
        onDelete = { }
    )
}

@Composable
fun DialogSession(
    record: TrackedActivityTime,
    onUpdate: ((LocalDateTime, LocalDateTime) -> Unit),
    onDelete: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit),
) = DialogSession(
    record.id > 0,
    record.datetime_start,
    record.datetime_end,
    onUpdate,
    onDismissRequest,
    onDelete
)


@Composable
fun DialogSession(
    allowDelete: Boolean,
    from: LocalDateTime,
    to: LocalDateTime,
    onUpdate: ((LocalDateTime, LocalDateTime) -> Unit),
    onDismissRequest: (() -> Unit),
    onDelete: (() -> Unit)? = null,
) = BaseDialog(onDismissRequest) {
    DialogSessionContent(allowDelete, from, to, onUpdate, onDismissRequest, onDelete)
}

@Composable
fun DialogSessionContent(
    allowDelete: Boolean,
    from: LocalDateTime,
    to: LocalDateTime,
    onUpdate: ((LocalDateTime, LocalDateTime) -> Unit),
    onDismissRequest: (() -> Unit),
    onDelete: (() -> Unit)? = null,
) = BaseDialog(
    onDismissRequest = onDismissRequest,
) {
    var from by remember { mutableStateOf(from) }
    var to by remember { mutableStateOf(to) }

    DialogBaseHeader(title = stringResource(id = if (allowDelete) R.string.dialog_session_title_edit else R.string.dialog_session_title_add))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        LabeledColumn(text = stringResource(id = R.string.session_start)) {
            EditableDatetime(
                datetime = from,
                onDatetimeEdit = {
                    from = it
                }
            )
        }

        LabeledColumn(text = stringResource(id = R.string.session_end)) {
            EditableDatetime(
                datetime = to,
                onDatetimeEdit = {
                    to = it
                }
            )
        }
    }

    val minutes = Duration.between(from, to).seconds / 60

    DurationText(minutes)

    DialogButtons {
        if (allowDelete) {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onDelete!!.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }
        }

        TextButton(onClick = { onDismissRequest() }) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(
            enabled = from < to && minutes <= 60 * 24,
            onClick = {
                onDismissRequest()
                onUpdate.invoke(from, to)
            }
        ) {
            Text(text = stringResource(id = if (allowDelete) R.string.dialog_action_edit else R.string.dialog_action_add))
        }
    }
}

@Composable
private fun ColumnScope.DurationText(minutes: Long) {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    val text = when {
        hours > 0 && remainingMinutes > 0 -> {
            pluralStringResource(R.plurals.hours, hours.toInt(), hours) + ", " +
                    pluralStringResource(
                        R.plurals.minutes,
                        remainingMinutes.toInt(),
                        remainingMinutes
                    )
        }

        hours > 0 -> {
            pluralStringResource(R.plurals.hours, hours.toInt(), hours)
        }

        else -> {
            pluralStringResource(R.plurals.minutes, remainingMinutes.toInt(), remainingMinutes)
        }
    }

    Text(
        text = text,
        modifier = Modifier
            .padding(top = 8.dp)
            .align(Alignment.CenterHorizontally)
    )
}

@Composable
private fun LabeledColumn(
    text: String,
    body: @Composable ColumnScope.() -> Unit,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        textAlign = TextAlign.Center,
        text = text,
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
    )

    body.invoke(this)
}

