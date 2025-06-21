package com.imfibit.activitytracker.ui.components.selectors


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.EditText
import com.imfibit.activitytracker.ui.components.icons.MinusOne


@ExperimentalFoundationApi
@Composable
fun NumberSelector(
    label: String,
    number: Int,
    range: IntRange,
    onNumberEdit: (Int)->Unit,
) {
    Row(Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 8.dp)) {

        val focusManager = LocalFocusManager.current

        Box(modifier = Modifier.padding(top = 15.dp).weight(50f)){
            IconButton(
                onClick = {
                    focusManager.clearFocus()

                    if (number - 1 in range){
                        onNumberEdit.invoke(number-1)
                    }
                },
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(Colors.ChipGray, RoundedCornerShape(50)),

                ) {
                Icon(Icons.Filled.MinusOne, contentDescription = null)
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(25f)
                .height(45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = label,
                modifier = Modifier.height(15.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )


            val localValue = remember(number) {
                mutableStateOf(TextFieldValue(text = number.toString(), selection = TextRange(number.toString().length)))
            }

            EditText(
                modifier = Modifier
                    .height(30.dp)
                    .focusRequester(FocusRequester.Default),
                text = localValue.value,

                onValueChange = {
                    val valid = when {
                        it.text.isEmpty() -> true
                        it.text.toIntOrNull() == null -> false
                        else ->  it.text.toInt() in range
                    }

                    if (valid)
                        localValue.value = it

                    if (valid && it.text.isNotBlank())
                        onNumberEdit.invoke(it.text.toInt())

                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardType = KeyboardType.Number,
                color = Colors.AppAccent
            )
        }

        Box(modifier = Modifier.padding(top = 15.dp).weight(50f)){
            IconButton(
                onClick = {
                    focusManager.clearFocus()

                    if (number + 1 in range){
                        onNumberEdit.invoke(number + 1)
                    }
                },
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(Colors.ChipGray, RoundedCornerShape(50)),
            ) {
                Icon(Icons.Filled.PlusOne, contentDescription = null)
            }

        }


    }



}

