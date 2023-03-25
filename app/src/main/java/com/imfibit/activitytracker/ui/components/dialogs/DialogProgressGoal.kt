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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TrackedActivityChallenge
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.BasicEditTextDecorationBox
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.dialogs.system.DialogDatePicker
import com.imfibit.activitytracker.ui.widgets.custom.GoalProgressBar
import java.time.LocalDate
import java.time.Period
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

        val defaultName  = stringResource(R.string.dialog_challenge_name_default)

        val challenge = remember(activity.challenge) {

            val value = if (activity.challenge.isSet()){
                activity.challenge
            }else{
                TrackedActivityChallenge(defaultName, 0, LocalDate.now(), LocalDate.now().plusMonths(1L))
            }

            mutableStateOf(value)
        }

        FormRow(name = stringResource(R.string.dialog_challenge_name)) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                value = challenge.value.name,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                onValueChange = {
                    if(it.length < 30){
                        challenge.value = challenge.value.copy(name = it)
                    }
                },
                decorationBox = { BasicEditTextDecorationBox (it) }
            )
        }

        val context = LocalContext.current

        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        FormRow(name = stringResource(R.string.dialog_challenge_from)) {
            RangeItem(
                modifier = Modifier.clickable {
                    DialogDatePicker(
                        date = challenge.value.from,
                        onDateSet = {
                            challenge.value = challenge.value.copy(from = it)
                        },
                        context = context
                    )
                },
                name = challenge.value.from.format(formatter)
            )
        }

        FormRow (stringResource(R.string.dialog_challenge_to)){
            RangeItem(
                modifier = Modifier.clickable {
                    DialogDatePicker(
                        date = challenge.value.to,
                        onDateSet = {
                            challenge.value = challenge.value.copy(to = it)
                        },
                        context = context
                    )
                },
                name = challenge.value.to.format(formatter)
            )
        }

        val label = when(activity.type){
            TrackedActivity.Type.TIME -> stringResource(R.string.dialog_challenge_target_time)
            TrackedActivity.Type.SCORE -> stringResource(R.string.dialog_challenge_target_score)
            TrackedActivity.Type.CHECKED -> stringResource(R.string.dialog_challenge_target_completions)
        }

        FormRow(name = label) {
            val toDisplay = if (challenge.value.target == 0L) "" else challenge.value.format(activity.type).toString()

            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                value = toDisplay ,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),

                onValueChange = {
                    val value = try { it.toLong() } catch (e: Exception) { 0L }

                    challenge.value = challenge.value.copy(target = if (activity.type == TrackedActivity.Type.TIME) value * 3600 else value)
                },
                decorationBox = { BasicEditTextDecorationBox (it) }
            )
        }

        val metric = remember{
            mutableStateOf(0L)
        }

        LaunchedEffect(challenge.value.from, challenge.value.to){
            metric.value = getLiveMetric(challenge.value.from, challenge.value.to)
        }

        GoalProgressBar(challenge.value, actual = metric.value, activity.type)

        if (!activity.goal.isSet() || !challenge.value.isSet()){
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.dialog_challenge_not_set), style = TextStyle(textAlign = TextAlign.Center)
            )
        }else if (challenge.value.target > metric.value){
            Column( modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val totalDays = ((challenge.value.target - metric.value) / activity.goal.metricPerDay()).toInt()

                val estimated = if (totalDays / 30 == 0){
                    stringResource(id = R.string.dialog_challenge_estimated_days, totalDays)
                }else{
                    stringResource(id = R.string.dialog_challenge_estimated_month_days, totalDays / 30, totalDays % 30)
                }

                val estimatedEndDate = LocalDate.now().plusDays(totalDays.toLong());

                val estimatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,)) {
                        append(estimated)
                    }

                    append(" - " + estimatedEndDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))
                }

                Text(text = stringResource(R.string.dialog_challenge_estimated_prefix))
                Text(text = estimatedString )
                
                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                val aheadInDays = Period.between(estimatedEndDate, challenge.value.to).days

                if (estimatedEndDate  > challenge.value.to ){
                    Text(text = stringResource(R.string.dialog_challenge_impossible), style = TextStyle(color = Color.Red, textAlign = TextAlign.Center) )
                }else{
                    Text(text =  stringResource(R.string.ahead_of_challenge_deadline_note, aheadInDays), style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center) )
                }

            }
        }

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
private fun FormRow(name: String, content: @Composable ()->Unit){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(130.dp))
        content()
    }
}

@Composable
private fun RangeItem(modifier: Modifier = Modifier, name: String){
    Box(
        modifier = modifier
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