package com.imfibit.activitytracker.ui.components.selectors

import android.util.Log
import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.ui.components.Colors


@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
inline fun MinuteAndHourSelector(
    hours: MutableState<Int?>,
    minutes: MutableState<Int?>,
    noinline onSelectionChanged: ((hours: Int, minutes:Int)->Unit)? = null
) {

    if (hours.value == 0) hours.value = null
    if (minutes.value == 0) minutes.value = null

    Row(
        Modifier.padding(8.dp, top = 16.dp).height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            Modifier.weight(50f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Hours",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)
            )

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(value = "ASD", onValueChange = {})

                BasicTextField(
                    value = hours.value?.toString() ?: "",
                    onValueChange = {
                        try {
                            it.toInt().let {
                                if (it in 0..9125) {
                                    hours.value = it
                                    onSelectionChanged?.invoke(hours.value ?: 0, minutes.value ?: 0)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

        }

        Column(Modifier.padding(horizontal = 8.dp)){
            Spacer(modifier = Modifier.height(22.dp))
            Box(Modifier.size(5.dp).background(Color.Black, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.height(4.dp))
            Box(Modifier.size(5.dp).background(Color.Black, RoundedCornerShape(50)))
        }

        Column(
            Modifier.weight(50f).padding(end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                textAlign = TextAlign.Center,
                text = "Minutes",
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
                    value = TextFieldValue(minutes.value?.toString() ?: ""),
                    onValueChange = {
                        try {
                            it.text.toInt().let {
                                if (it in 0..59){
                                    minutes.value = it
                                    onSelectionChanged?.invoke(hours.value ?: 0, minutes.value ?: 0)
                                }
                            }
                        }catch (e: Exception){}
                    },
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

            }

        }

    }

}



