package com.imfibit.activitytracker.ui.components.dialogs

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.database.Seeder
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryTrackedActivity
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.layout.LabeledColumn
import io.burnoutcrew.reorderable.*
import java.time.LocalDateTime

@Preview
@Composable
fun ComposablePreview() {

    val x = remember {
        mutableStateOf(true)
    }

    DialogTimers(
        display = x,
        activity = Seeder().getTrackedActivity(),
        listOf()
    )
}


fun Modifier.draggedItemx(
    offset: Float?,
    orientation: Orientation = Orientation.Vertical,
): Modifier = composed {
    Modifier
        .zIndex(offset?.let { 1f } ?: 0f)
        .graphicsLayer {
            with(offset ?: 0f) {
                if (orientation == Orientation.Vertical) {
                    translationY = this
                } else {
                    translationX = this
                }
            }
        }
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



    Row(
        modifier = Modifier
            .padding(top = 16.dp, start = 12.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(30.dp)
                .background(Colors.ChipGray, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text ="Jednorázový",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }



    }

    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Text(text = "Přednastavené", fontWeight = FontWeight.W500, fontSize = 20.sp, modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(30.dp)
                .background(Colors.ChipGray, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier
                .size(30.dp))
        }

    }

    val items = timers.toMutableList()


    val state: ReorderableState = rememberReorderState(
        onDragEnd = { from, to -> },
        onMove = { from, to -> items.move(from, to) }
    )


    LazyColumn(
        state = state.listState,
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp).reorderable(state)){

        itemsIndexed(items){ index, item ->

            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)){

            }





            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.draggedItemx(state.offset.takeIf { Log.e("xxx", "take") ; state.index == index }),
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


                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier
                            .align(Alignment.CenterEnd))
                    }

                }

            }


        }

    }

}