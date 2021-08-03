package com.imfibit.activitytracker.ui.components.selectors


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.lang.Exception


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun NumberSelector(
    label: String,
    number: MutableState<Int>,
    onNumberEdit: (Int)->Unit,
) {

    Row(Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 8.dp)) {
        IconButton(
            onClick = {
                if (number.value in Int.MIN_VALUE+1..Int.MAX_VALUE){
                    onNumberEdit.invoke(number.value-1)
                }
            },
            modifier = Modifier
                .padding(end = 8.dp, top = 15.dp)
                .height(30.dp)
                .weight(50f)
                .background(Colors.ChipGray, RoundedCornerShape(50)),

            ) {
            Icon(Icons.Filled.MinusOne, contentDescription = null)
        }


        Column(
            modifier = Modifier.weight(25f).height(45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = label,
                modifier = Modifier.height(15.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )

            EditText(
                modifier = Modifier.height(30.dp),
                text = TextFieldValue(number.value.toString()),
                onValueChange = {
                    try {
                        onNumberEdit.invoke(it.text.toInt())
                    } catch (e: Exception) { }
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

        Box(
            modifier = Modifier
                .padding(start = 8.dp, top = 15.dp).weight(50f)
                .background(Colors.ChipGray, RoundedCornerShape(50))
                .padding(start = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    if (number.value in Int.MIN_VALUE until Int.MAX_VALUE){
                        onNumberEdit.invoke(number.value + 1)
                    }
                },
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Filled.PlusOne, contentDescription = null)
            }
        }

    }



}

