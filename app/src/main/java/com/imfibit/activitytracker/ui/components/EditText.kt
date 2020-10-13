package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview


@Preview
@Composable
fun EditTextPrev(){

    Stack(Modifier.size(300.dp, 300.dp)) {
        EditText(text = TextFieldValue(""), onValueChange = {}, modifier = Modifier.size(200.dp, 30.dp))
    }

}



@OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)

@Composable
fun EditText(
    text: TextFieldValue,
    onValueChange: (TextFieldValue)->Unit,
    validate: (TextFieldValue)->Boolean = {true},
    modifier: Modifier = Modifier,
    label: String = "",
    textStyle:TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    keyboardType: KeyboardType = KeyboardType.Text,
    color: Color = Colors.ChipGray
){
    val valid = validate(text)

    val color = if (valid) color else Colors.NotCompleted

    Stack(modifier.background(color, shape = RoundedCornerShape(50)), alignment = Alignment.Center) {

        if (text.text.isEmpty())
            Text(text = label)

        BaseTextField(
            value = text,
            onValueChange = onValueChange,
            textStyle = textStyle,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxWidth(),
            keyboardType = keyboardType,
        )
    }
}

