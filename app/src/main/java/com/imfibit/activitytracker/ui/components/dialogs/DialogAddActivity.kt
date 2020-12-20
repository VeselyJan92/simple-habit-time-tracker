package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Score
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DialogAddActivity(
    nav: NavController,
    display: MutableState<Boolean>,
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = stringResource(id = R.string.create_new_activity))

        val rep = AppDatabase.activityRep

        val name =  stringResource(id = R.string.new_activity_name)

        val insert = fun(type: TrackedActivity.Type) = GlobalScope.launch(Dispatchers.IO) {
            val activity = TrackedActivity(
                id = 0L,
                name =name,
                position = 0,
                type = type,
                inSessionSince = null,
                goal = TrackedActivityGoal(0L, TimeRange.WEEKLY)
            )

            val id = rep.activityDAO.insertSync(activity)

            withContext(Dispatchers.Main){
                display.value = false
                
                nav.navigate("screen_activity/$id")
            }
        }

        TrackedActivities(Icons.Default.Timer, stringResource(id = R.string.new_activity_time)){
            insert.invoke(TrackedActivity.Type.TIME)
        }

        TrackedActivities(Icons.Default.Score, stringResource(id = R.string.new_activity_score)){
            insert.invoke(TrackedActivity.Type.SCORE)
        }

        TrackedActivities(Icons.Default.AssignmentTurnedIn, stringResource(id = R.string.new_activity_checked)) {
            insert.invoke(TrackedActivity.Type.CHECKED)
        }

        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = stringResource(id = R.string.cancel))
            }

        }
    }

}



@Composable
private fun TrackedActivities(
    icon: ImageVector,
    name: String,
    clickable: () -> Unit
){
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .zIndex(1f)
            .shadow(2.dp, shape = RoundedCornerShape(10.dp))
            .background(Colors.ChipGray, RoundedCornerShape(10.dp))
            .clickable(onClick = clickable, indication = rememberRipple())
            .padding(8.dp)

    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Modifier.padding(end = 16.dp)

            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = icon
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