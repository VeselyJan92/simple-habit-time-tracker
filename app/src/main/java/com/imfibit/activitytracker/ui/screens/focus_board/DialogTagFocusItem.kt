package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.DevSeeder
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.ui.components.AppTextField
import com.imfibit.activitytracker.ui.components.AppTextFieldStyle_Header
import com.imfibit.activitytracker.ui.components.dialogs.BaseDialog
import com.imfibit.activitytracker.ui.components.dialogs.DialogBaseHeader
import com.imfibit.activitytracker.ui.components.dialogs.DialogButtons
import kotlin.math.ceil


@Preview()
@Composable
private fun Preview() {

    val edit = remember {
        mutableStateOf(true)
    }

    DialogEditTag(
        edit, true, DevSeeder.getFocusBoardItemTag(), {}, {}
    )
}

@Composable
fun DialogEditTag(
    display: MutableState<Boolean>,
    isEdit: Boolean,
    item: FocusBoardItemTag = FocusBoardItemTag(),
    onTagEdit: (FocusBoardItemTag)->Unit,
    onTagDelete: (FocusBoardItemTag)->Unit = {},
){
    BaseDialog(display = display ) {
        val dialogTitle = if (isEdit) R.string.dialog_edit_tag_title else R.string.dialog_create_tag_title

        DialogBaseHeader(title = stringResource(id = dialogTitle))

        val color = remember {
            mutableIntStateOf(item.color)
        }

        val name = remember {
            mutableStateOf(TextFieldValue(item.name))
        }


        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)){

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name.value,
                onValueChange = {name.value = it},
                style = AppTextFieldStyle_Header,
                placeholderText = stringResource(R.string.focus_board_edit_tag_name_placeholder)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Layout(
                modifier = Modifier,
                content = {
                    FocusBoardItemTag.colors.forEach {
                        val modifier = if (it.toArgb() == color.intValue){
                            Modifier.border(2.dp, Color.Black, RoundedCornerShape(5.dp))
                        }else{
                            Modifier
                        }

                        Box(modifier = modifier
                            .size(50.dp)
                            .background(it, RoundedCornerShape(5.dp))
                            .clickable {
                                color.intValue = it.toArgb()
                            },
                        )

                    }
                },
                measurePolicy = { measurables, constraints  ->
                    val placeables = measurables.map { measurable -> measurable.measure(constraints) }

                    val size = placeables.first().height

                    var boxes = constraints.maxWidth / size

                    var space = (constraints.maxWidth - size * boxes) / (boxes -1)

                    // Check for too small padding
                    if (space < 4.dp.toPx()){
                        boxes -= 1
                        space = (constraints.maxWidth - size * boxes) / (boxes -1)
                    }

                    val totalHeight = ceil( placeables.size.toDouble() / boxes).toInt() * (size + space) - space

                    layout(constraints.maxWidth, totalHeight) {
                        var xPosition = 0
                        var yPosition = 0

                        // Place children in the parent layout
                        placeables.forEachIndexed { index, placeable ->

                            placeable.place(xPosition, yPosition)

                            if (index % boxes == boxes -1 && index != 0 ){
                                yPosition += size + space
                                xPosition = 0
                            }else{
                                xPosition += size + space
                            }
                        }
                    }
                }
            )
        }

        DialogButtons {

            if(isEdit){
                TextButton(
                    onClick = {
                        onTagDelete(item)
                        display.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dialog_action_delete))
                }
            }

            TextButton(onClick = { display.value = false} ) {
                Text(text = stringResource(id = R.string.dialog_action_cancel))
            }

            TextButton(
                onClick = {
                    val editedItem = item.copy(name = name.value.text, color = color.intValue)

                    onTagEdit(editedItem)
                    display.value = false
                },
                enabled = name.value.text.isNotEmpty()
            ) {
                Text(text = stringResource(id = R.string.dialog_action_continue))
            }
        }
    }

}