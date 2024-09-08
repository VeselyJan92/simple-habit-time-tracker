package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Score
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors


@Composable
fun DialogAddActivity(
    display: MutableState<Boolean>,
    onAddActivity: (type: TrackedActivity.Type)->Unit,
    onAddFolder: ()->Unit,
    onAddFocusItem: ()->Unit
) = BaseDialog(display = display ) {

    DialogBaseHeader(title = stringResource(id = R.string.create_new_activity))


    TrackedActivities(modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_TIME), Icons.Default.Timer, stringResource(id = R.string.new_activity_time)){
        onAddActivity.invoke(TrackedActivity.Type.TIME)
    }

    TrackedActivities(modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_SCORE), Icons.Default.Score, stringResource(id = R.string.new_activity_score)){
        onAddActivity.invoke(TrackedActivity.Type.SCORE)
    }

    TrackedActivities(modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_COMPLETION), Icons.Default.AssignmentTurnedIn, stringResource(id = R.string.new_activity_checked)) {
        onAddActivity.invoke(TrackedActivity.Type.CHECKED)
    }

    TrackedActivities(modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_GROUP), Icons.Default.Topic, stringResource(id = R.string.new_activity_folder)) {
        onAddFolder()
    }

    TrackedActivities(modifier = Modifier.testTag(TestTag.DIALOG_ADD_ACTIVITY_FOCUS_ITEM), Icons.Default.Assignment, stringResource(id = R.string.new_activity_focus_item)) {
        onAddFocusItem()
    }

    DialogButtons {

        TextButton(onClick = {display.value = false} ) {
            Text(text = stringResource(id = R.string.cancel))
        }

    }
}


@Composable
private fun TrackedActivities(
    modifier: Modifier,
    icon: ImageVector,
    name: String,
    clickable: () -> Unit
){

    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .zIndex(1f)
            .shadow(2.dp, shape = RoundedCornerShape(10.dp))
            .background(Colors.ChipGray, RoundedCornerShape(10.dp))
            .clickable(onClick = clickable)
            .padding(8.dp)

    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Modifier.padding(end = 16.dp)

            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = icon,
                contentDescription = ""
            )

            Column {
                Text(
                    text = name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}