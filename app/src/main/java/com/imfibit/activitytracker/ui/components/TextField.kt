package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Preview()
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(24.dp).background(Color.White),  ){
        AppTextField(
            modifier = Modifier.width(150.dp),
            value = TextFieldValue(""),
            onValueChange = {},
            placeholderText = "Placeholder"
        )
    }
}

val AppTextFieldStyle_Header = TextStyle.Default.copy(fontSize = 17.sp, fontWeight = FontWeight.SemiBold)

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    style: TextStyle = TextStyle(),
    placeholderText: String = "",
    placeholder: @Composable ()-> Unit = {
        Text(text = placeholderText, style = style.copy(color = Color.LightGray, fontWeight = FontWeight.Normal))
    },
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
) {
    BasicTextField(
        modifier = modifier,
        textStyle = style,
        singleLine = singleLine,
        value = value,
        keyboardOptions = keyboardOptions,
        onValueChange = onValueChange,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .background(Colors.SuperLight, RoundedCornerShape(5.dp))
                    .padding(8.dp)
            ){
                if (value.text.isEmpty()){
                    placeholder()
                }

                innerTextField()
            }
        }
    )

}