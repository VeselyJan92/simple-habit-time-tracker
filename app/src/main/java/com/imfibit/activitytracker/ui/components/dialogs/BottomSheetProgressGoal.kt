package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.ScrollBottomSheet
import com.imfibit.activitytracker.ui.components.rememberAppBottomSheetState
import com.imfibit.activitytracker.ui.components.rememberTestBottomSheetState
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DialogProgressGoal_Preview() = AppTheme {
    BottomSheetProgressGoal(
        activity = DevSeeder.getTrackedActivityTime(),
        state = rememberTestBottomSheetState(),
        getLiveMetric = { _, _ -> 0L },
        onSet = { },
        onDelete = { },
        onDismissRequest = { }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetProgressGoal(
    state: SheetState = rememberAppBottomSheetState(),
    activity: TrackedActivity,
    onDismissRequest: () -> Unit,
    getLiveMetric: suspend (LocalDate?, LocalDate?) -> Long,
    onSet: (TrackedActivityChallenge) -> Unit,
    onDelete: () -> Unit,
) = ScrollBottomSheet(
    state = state,
    onDismissRequest = onDismissRequest,
) { onDismissRequest ->


    val defaultName = stringResource(R.string.dialog_challenge_name_default)

    val challenge = remember(activity.challenge) {

        val value = if (activity.challenge.isSet()) {
            activity.challenge
        } else {
            TrackedActivityChallenge(
                defaultName,
                0,
                LocalDate.now(),
                LocalDate.now().plusMonths(1L)
            )
        }

        mutableStateOf(value)
    }


    Text(
        text = stringResource(R.string.dialog_challenge_title),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        label = { Text(text = stringResource(R.string.dialog_challenge_name)) },
        modifier = Modifier
            .fillMaxWidth(),
        value = challenge.value.name,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        onValueChange = {
            if (it.length < 30) {
                challenge.value = challenge.value.copy(name = it)
            }
        },
    )

    Spacer(modifier = Modifier.height(8.dp))

    RangeItem(
        label = stringResource(R.string.dialog_challenge_from),
        date = challenge.value.from,
        onDateSet = {
            challenge.value = challenge.value.copy(from = it)
        },
    )

    Spacer(modifier = Modifier.height(8.dp))


    RangeItem(
        label = stringResource(R.string.dialog_challenge_to),
        date = challenge.value.to,
        onDateSet = {
            challenge.value = challenge.value.copy(to = it)
        },
    )

    Spacer(modifier = Modifier.height(8.dp))


    val label = when (activity.type) {
        TrackedActivity.Type.TIME -> stringResource(R.string.dialog_challenge_target_time)
        TrackedActivity.Type.SCORE -> stringResource(R.string.dialog_challenge_target_score)
        TrackedActivity.Type.CHECKED -> stringResource(R.string.dialog_challenge_target_completions)
    }


    val toDisplay =
        if (challenge.value.target == 0L) "" else challenge.value.format(activity.type)
            .toString()

    OutlinedTextField(
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth(),
        value = toDisplay,
        singleLine = true,
        onValueChange = {
            val value = try {
                it.toLong()
            } catch (e: Exception) {
                0L
            }

            challenge.value =
                challenge.value.copy(target = if (activity.type == TrackedActivity.Type.TIME) value * 3600 else value)
        },
    )

    val metric = remember {
        mutableStateOf(0L)
    }

    LaunchedEffect(challenge.value.from, challenge.value.to) {
        metric.value = getLiveMetric(challenge.value.from, challenge.value.to)
    }

    Spacer(modifier = Modifier.height(24.dp))

    GoalProgressBar(challenge.value, actual = metric.value, activity.type)

    Spacer(modifier = Modifier.height(8.dp))

    if (!activity.goal.isSet() || !challenge.value.isSet()) {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.dialog_challenge_not_set),
            style = TextStyle(textAlign = TextAlign.Center)
        )
    } else if (challenge.value.target > metric.value) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val totalDays =
                ((challenge.value.target - metric.value) / activity.goal.metricPerDay()).toInt()

            val estimated = if (totalDays / 30 == 0) {
                stringResource(id = R.string.dialog_challenge_estimated_days, totalDays)
            } else {
                stringResource(
                    id = R.string.dialog_challenge_estimated_month_days,
                    totalDays / 30,
                    totalDays % 30
                )
            }

            val estimatedEndDate = LocalDate.now().plusDays(totalDays.toLong());

            val estimatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(estimated)
                }

                append(
                    " - " + estimatedEndDate.format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.LONG
                        )
                    )
                )
            }

            Text(text = stringResource(R.string.dialog_challenge_estimated_prefix))
            Text(text = estimatedString)

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            val aheadInDays = Period.between(estimatedEndDate, challenge.value.to).days

            if (estimatedEndDate > challenge.value.to) {
                Text(
                    text = stringResource(R.string.dialog_challenge_impossible),
                    style = TextStyle(color = Color.Red, textAlign = TextAlign.Center)
                )
            } else {
                Text(
                    text = stringResource(
                        R.string.ahead_of_challenge_deadline_note,
                        aheadInDays
                    ),
                    style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center)
                )
            }

        }
    }

    DialogButtons {
        TextButton(onClick = { onDismissRequest() }) {
            Text(text = stringResource(id = R.string.dialog_action_cancel))
        }

        TextButton(onClick = { onDismissRequest { onDelete() } }) {
            Text(text = stringResource(id = R.string.dialog_action_delete))
        }

        TextButton(
            onClick = {
                if (challenge.value.target != 0L && challenge.value.name.isNotBlank()) {
                    onDismissRequest {
                        onSet(challenge.value)
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.dialog_action_continue))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeItem(
    label: String,
    date: LocalDate, onDateSet: (LocalDate) -> Unit,
) {
    val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        val dateState = rememberDatePickerState(initialSelectedDate = date)

        DatePickerDialog(
            onDismissRequest = {
                showPicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSet(dateState.getSelectedDate() ?: LocalDate.now())
                        showPicker = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_continue))
                }
            }
        ) {
            DatePicker(
                state = dateState,
            )
        }
    }

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) {
                    showPicker = true
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    OutlinedTextField(
        label = { Text(text = label) },
        value = date.format(formatter),
        onValueChange = { /* This field is not directly editable */ },
        modifier = Modifier
            .clickable { showPicker = true }
            .fillMaxWidth(),

        interactionSource = interactionSource,
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date"
            )
        }
    )
}