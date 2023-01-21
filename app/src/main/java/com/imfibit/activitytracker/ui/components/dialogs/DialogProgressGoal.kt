package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogDatePicker
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun DialogProgressGoal(
    display: MutableState<Boolean>,
    activity: TrackedActivity,
    getLiveMetric: suspend (LocalDate?, LocalDate?) -> Long,
    onSet: (TrackedActivityChallenge)->Unit,
    onDelete: ()->Unit
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = stringResource(R.string.dialog_challenge_title))

        val modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()


        val challenge = remember(activity.challenge) {
            mutableStateOf(activity.challenge)
        }

        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, ) {
            Label(stringResource(R.string.dialog_challenge_name))

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(
                        Colors.ChipGray,
                        RoundedCornerShape(50)
                    ),

                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = challenge.value.name,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    onValueChange = {
                        if(it.length < 30){
                            challenge.value = challenge.value.copy(name = it)
                        }
                    },
                )
            }
        }

        val context = LocalContext.current

        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        Row(
            modifier = modifier.clickable {
                DialogDatePicker(
                    date = challenge.value.from ?: LocalDate.now(),
                    onDateSet = {
                        challenge.value = challenge.value.copy(from = it)
                    },
                    context = context
                )
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
                Label(text = stringResource(R.string.dialog_challenge_from))

                RangeItem(name = challenge.value.from?.format(formatter) ?: stringResource(R.string.dialog_challenge_date_not_set) )
        }

        Row(
            modifier = modifier.clickable {
                DialogDatePicker(
                    date = challenge.value.to ?: LocalDate.now(),
                    onDateSet = {
                        challenge.value = challenge.value.copy(to = it)
                    },
                    context = context
                )
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Label(text = stringResource(R.string.dialog_challenge_to))

            RangeItem(name = challenge.value.to?.format(formatter) ?: stringResource(R.string.dialog_challenge_date_not_set)  )
        }

        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, ) {
            val label = when(activity.type){
                TrackedActivity.Type.TIME -> stringResource(R.string.dialog_challenge_target_time)
                TrackedActivity.Type.SCORE -> stringResource(R.string.dialog_challenge_target_score)
                TrackedActivity.Type.CHECKED -> stringResource(R.string.dialog_challenge_target_completions)
            }

            Label(label)

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(
                        Colors.ChipGray,
                        RoundedCornerShape(50)
                    ),

                contentAlignment = Alignment.Center
            ) {

                val toDisplay = if (challenge.value.target == 0L) "" else challenge.value.format(activity.type).toString()

                BasicTextField(
                    value = toDisplay ,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),

                    onValueChange = {
                        val value = try { it.toLong() } catch (e: Exception) { 0L }

                        challenge.value = challenge.value.copy(target = if (activity.type == TrackedActivity.Type.TIME) value * 3600 else value)
                    },
                )
            }
        }

        val metric = remember{
            mutableStateOf(0L)
        }

        LaunchedEffect(challenge.value.from, challenge.value.to){
            metric.value = getLiveMetric(challenge.value.from, challenge.value.to)
        }

        GoalProgressBar(challenge.value, actual = metric.value, activity.type)


        DialogButtons {
            TextButton(onClick = { display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(onClick = { onDelete() ; display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_delete))
            }

            TextButton(
                onClick = {
                    if (challenge.value.target != 0L && challenge.value.name.isNotBlank()){
                        onSet(challenge.value)
                        display.value = false
                    }
                }

            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }


}

@Composable
private fun Label(text: String){
    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(130.dp))
}

@Composable
private fun RangeItem(name: String){
    Box(
        modifier = Modifier
            .height(30.dp)
            .fillMaxWidth()
            .background(
                Colors.ChipGray,
                RoundedCornerShape(50)
            ),

        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}