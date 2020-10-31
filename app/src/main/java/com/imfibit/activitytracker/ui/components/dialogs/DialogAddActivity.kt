package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Score
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.getitdone.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
fun DialogAddActivity(
    nav: NavController,
    display: MutableState<Boolean>,
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = "Create new activity")

        val rep = AppDatabase.activityRep

        val insert = fun(type: TrackedActivity.Type) = GlobalScope.launch(Dispatchers.IO) {
            val activity = TrackedActivity(
                id = 0L,
                name = "New Activity",
                position = 0,
                type = type,
                inSessionSince = null,
                goal = TrackedActivityGoal(0L, TimeRange.WEEKLY)
            )

            val id = rep.activityDAO.insertSync(activity)

            withContext(Dispatchers.Main){
                display.value = false

                nav.navigate(
                    R.id.action_navigation_dashboard_to_activity_fragment,
                    bundleOf("tracked_activity_id" to id)
                )
            }
        }

        TrackedActivities(Icons.Default.Timer, "Time", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... "){
            insert.invoke(TrackedActivity.Type.SESSION)
        }

        TrackedActivities(Icons.Default.Score, "Score", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... "){
            insert.invoke(TrackedActivity.Type.SCORE)
        }

        TrackedActivities(Icons.Default.AssignmentTurnedIn, "Habbit", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... ") {
            insert.invoke(TrackedActivity.Type.CHECKED)
        }

        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = "ZPĚT")
            }

        }
    }

}



@Composable
private fun TrackedActivities(
    icon: VectorAsset,
    name: String,
    desc: String,
    clickable: () -> Unit
){
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .zIndex(1f)
            .drawShadow(2.dp, shape = RoundedCornerShape(10.dp))
            .background(Colors.ChipGray, RoundedCornerShape(10.dp))
            .clickable(onClick = clickable, indication = RippleIndication())
            .padding(8.dp)

    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Modifier.padding(end = 16.dp)
            Icon(icon)

            Column {
                Text(
                    text = name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = desc,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    }
}