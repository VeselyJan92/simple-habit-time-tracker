package com.imfibit.activitytracker.ui.components.selectors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors


private data class State(
        val hours: MutableState<TextFieldValue>,
        val minutes: MutableState<TextFieldValue>
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun MinuteAndHourSelector(
    hours: Int,
    minutes: Int,
    onSelectionChanged: ((hours: Int, minutes:Int)->Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    fun format(number: Int) = if (number == 0) "" else number.toString()

    val state = remember {
        State(
            mutableStateOf(TextFieldValue(format(hours))),
            mutableStateOf(TextFieldValue(format(minutes)))
        )
    }

    Row(
            Modifier.padding(top = 16.dp).height(50.dp),
            horizontalArrangement = Arrangement.Center
    ) {

        TimeEntry(state, stringResource(id = R.string.hours), state.hours, 0..9125, onSelectionChanged)

        Column(Modifier.padding(horizontal = 8.dp)){
            Spacer(modifier = Modifier.height(22.dp))
            Box(Modifier.size(5.dp).background(Color.Black, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.height(4.dp))
            Box(Modifier.size(5.dp).background(Color.Black, RoundedCornerShape(50)))
        }

        TimeEntry(state, stringResource(id = R.string.minutes), state.minutes, 0..59, onSelectionChanged)
    }

}

@Composable
private fun TimeEntry(
        state: State,
        label: String,
        holder: MutableState<TextFieldValue>,
        validRange: IntRange,
        onSelectionChanged: ((hours: Int, minutes:Int)->Unit)? = null

){
    Column(
            Modifier.width(110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            textAlign = TextAlign.Center,
            text = label,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
        )

        Box(
            modifier = Modifier
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = holder.value,
                onValueChange = { field ->
                    if (field.text.isEmpty()){
                        holder.value = TextFieldValue()

                    } else{
                        try {
                            if (field.text.toInt() in validRange) {
                                holder.value = field
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    onSelectionChanged?.invoke(
                        state.hours.value.text.toIntOrNull() ?: 0,
                        state.minutes.value.text.toIntOrNull() ?: 0
                    )

                },
                textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        }

    }

}




