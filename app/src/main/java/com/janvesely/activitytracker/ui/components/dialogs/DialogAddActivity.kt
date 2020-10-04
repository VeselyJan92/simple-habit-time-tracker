package com.janvesely.activitytracker.ui.components.dialogs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.getitdone.database.AppDatabase


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
fun DialogAddActivity(
    display: MutableState<Boolean>,
){
    BaseDialog(display = display ) {

        DialogBaseHeader(title = "Create new activity")

        val rep = AppDatabase.activityRep

        TrackedActivities(Icons.Default.Timer, "Time", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... "){
            rep.insert(TrackedActivity())
        }

        TrackedActivities(Icons.Default.Score, "Score", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... "){

        }

        TrackedActivities(Icons.Default.AssignmentTurnedIn, "Habbit", "Zaznávejte svoje časové aktivity: práce,  kníčky, ... ") {

        }


        DialogButtons {

            TextButton(onClick = {display.value = false} ) {
                Text(text = "ZPĚT")
            }

        }
    }

}

@Composable
private fun InfoPanel(){
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .zIndex(1f)
            .drawShadow(2.dp, shape = RoundedCornerShape(20.dp))
            .background(Colors.ChipGraySelected, RoundedCornerShape(20.dp))
            .padding(8.dp)


    ){
        Text(
            text = "Záznamy",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )

        Text(
            text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Curabitur vitae diam non enim vestibulum interdum.",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)

        )
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
            Icon(icon, modifier = Modifier.padding(end = 16.dp))

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