package com.janvesely.activitytracker.ui.activities.composable

import android.util.Log
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.database.Seeder
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.components.Colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalDateTime

@Preview
@Composable
fun LiveActivitiesList(){

    LazyColumnForIndexed(
        modifier = Modifier.clip(RoundedCornerShape(topLeft = 20.dp, topRight = 20.dp)).background(Color.White),
        items = listOf(
            Seeder().getTrackedActivity(inSession = LocalDateTime.now()),
            Seeder().getTrackedActivity(inSession = LocalDateTime.now())
        )
    ) { index: Int, item: TrackedActivity ->

        if (index != 0){
            Divider(color = Colors.ChipGray, thickness = 1.dp)
        }

        Row(Modifier.fillParentMaxWidth().height(56.dp), verticalGravity = Alignment.CenterVertically) {

            IconButton(onClick = {}) {
                Box(Modifier.size(20.dp, 20.dp).background(Color.Red))
            }

            Text(text = item.name, Modifier.weight(1f), style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ))

            Row(
                modifier = Modifier
                    .size(130.dp, 25.dp)
                    .padding(end = 8.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50))

            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    Modifier.gravity(Alignment.CenterVertically).padding(start = 5.dp).size(20.dp)
                )

                val (text, setTime) = remember {  mutableStateOf("00:00:00") }

                launchInComposition{
                    while (this.coroutineContext.isActive){
                        val seconds = Duration.between(item.in_session_since, LocalDateTime.now()).seconds

                        val h = (seconds / 3600).toInt()
                        val m = (seconds - h * 3600).toInt() / 60
                        val s = (seconds - h * 3600 - m * 60).toInt() / 1

                        setTime((if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s)
                        delay(100)
                    }

                }

                Text(text,
                    Modifier.weight(1f).gravity(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                )


            }


        }
    }

}
@Composable
fun StaticActivity(){

}