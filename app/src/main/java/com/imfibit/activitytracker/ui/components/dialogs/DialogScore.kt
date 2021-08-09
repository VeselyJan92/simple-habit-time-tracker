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


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DialogScore(
    display: MutableState<Boolean> = mutableStateOf(true),
    record: TrackedActivityScore,
    onUpdate: ((LocalDateTime, Long)->Unit),
    onDelete: (() -> Unit)? = null
) = DialogScore(display, true, record.score, record.datetime_completed, onUpdate, onDelete)


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DialogScore(
    display: MutableState<Boolean> = mutableStateOf(true),
    allowDelete: Boolean,
    score: Long,
    datetime: LocalDateTime,
    onUpdate: ((LocalDateTime, Long)->Unit),
    onDelete: (() -> Unit)? = null
) {
    val score = remember { mutableStateOf(score.toInt()) }
    val datetime = remember { mutableStateOf(datetime) }

    BaseDialog(display = display) {

        DialogBaseHeader(title = stringResource(id = if (allowDelete) R.string.dialo_score_title_edit else R.string.dialo_score_title_add))

        NumberSelector(label = stringResource(id = R.string.score), number = score){
            if ( it in 1..999) score.value = it
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
                Modifier.height(30.dp).background(Colors.ChipGray, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                DatetimeEditor(datetime = datetime)
            }
        }

        DialogButtons {
            if (onDelete != null){
                TextButton(
                    onClick = {
                        display.value = false
                        onDelete.invoke()
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
            }

            TextButton(onClick = {  display.value = false }) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = {
                display.value = false
                onUpdate.invoke(datetime.value, score.value.toLong())

            }) {
                Text(text = stringResource(id = if (onDelete != null) R.string.dialog_action_edit else R.string.dialog_action_add))

            }

        }
    }

}

