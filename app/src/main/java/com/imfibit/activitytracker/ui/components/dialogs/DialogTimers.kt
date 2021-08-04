package com.imfibit.activitytracker.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.layout.LabeledColumn
import java.time.LocalDateTime


@Composable
fun ComposablePreview() {
    Box(modifier = Modifier
        .background(Color.Blue)
        .size(50.dp))

 /*   val x = remember {
        mutableStateOf(true)
    }

    DialogTimers(
        display = x,
        activity = Seeder().getTrackedActivity())*/
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun DialogTimers(
        display: MutableState<Boolean> = mutableStateOf(true),
        activity: TrackedActivity,
        timers: List<PresetTimer>,
        noinline onSetSession: ((LocalDateTime, LocalDateTime)->Unit)? = null
)  = BaseDialog(display = display) {

    DialogBaseHeader(title = stringResource( R.string.dialog_session_title_add))


    //Text(text = "Časovače", fontWeight = FontWeight.W500, fontSize = 20.sp, modifier = Modifier.padding(8.dp))

    Row(
        modifier = Modifier
            .padding(top = 16.dp, start = 12.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabeledColumn(text = "Vlastní časovač") {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .background(Colors.ChipGray, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text ="00:00",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }



        Box(
            Modifier
                .padding(start = 48.dp)
                .size(50.dp, 30.dp)
                .background(Colors.ButtonGreen, RoundedCornerShape(50)), contentAlignment = Alignment.Center){
            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
        }

    }

    Text(text = "Časovače", fontWeight = FontWeight.W500, fontSize = 20.sp, modifier = Modifier.padding(8.dp))

    LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)){

        itemsIndexed(timers){ index, item ->

            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)){

            }

            SwipeToDismiss(
                state = dismissState,
                background = {}
            ) {
                Row(modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(30))
                    .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {




                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .background(Colors.ChipGray, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text ="00:00",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    /*Box(Modifier.size(40.dp, 30.dp), contentAlignment = Alignment.Center){
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }*/

                    Box(modifier = Modifier.fillMaxWidth().padding(end = 8.dp)) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier
                            .size(30.dp).align(Alignment.CenterEnd))
                    }

                }

            }


        }

    }

}