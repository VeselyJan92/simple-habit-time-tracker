package com.imfibit.activitytracker.ui.screens.timer_over

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.VibrationEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar
import com.imfibit.activitytracker.ui.components.dialogs.DialogAddActivity
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat.getSystemService

import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.material.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.imfibit.activitytracker.ui.components.RoundTextBox
import com.imfibit.activitytracker.ui.components.Timer
import java.time.LocalDateTime


@Composable
fun ScreenTimerOver(
    navController: NavHostController,
){

    val vm = viewModel<TimerOverViewModel>()
    val display = remember { mutableStateOf(false) }

    DialogAddActivity(navController, display = display)

    Scaffold(
            topBar = { TrackerTopAppBar(stringResource(id = R.string.screen_title_activities)) },

            content = {
                Body()
            },

            backgroundColor = Colors.AppBackground
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
private fun Body(){
    Surface(elevation = 2.dp, modifier = Modifier.padding(8.dp)) {

        val x = LocalContext.current

        var builder = NotificationCompat.Builder(x, "timer")
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        builder.build()




            // val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            // vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))


            val blink = remember{ mutableStateOf(true) }

            val time = remember { LocalDateTime.now().minusMinutes(20L) }


            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(modifier = Modifier.padding(top = 50.dp))


                RoundTextBox(modifier = Modifier.size(300.dp, 50.dp), text = "Škola", style =TextStyle(fontSize = 30.sp, fontWeight = FontWeight.W700) )




                Box(modifier =Modifier.size(210.dp), contentAlignment = Alignment.Center ){

                    if (blink.value){
                        Icon(Icons.Default.NotificationsActive, null, modifier = Modifier
                            .size(200.dp)
                            .padding(top = 50.dp)
                        )
                    }

                }


                Text(text = "Máte hotovo", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.W700))

                Spacer(modifier = Modifier.padding(top = 50.dp))


                Timer(startTime = time)


                Row(
                    modifier = Modifier.padding(top=50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val style = TextStyle(fontWeight = FontWeight.W400, fontSize = 18.sp)

                    RoundTextBox(modifier = Modifier.size(150.dp, 50.dp), text = "Zastavit", color = Colors.NotCompleted, style =style )

                    Spacer(modifier = Modifier.size(20.dp))

                    RoundTextBox(modifier = Modifier.size(150.dp, 50.dp), text = "Pokračovat", color = Colors.ButtonGreen, style =style )

                }


            }

            LaunchedEffect(blink){
                while (true){
                    delay(500)

                    blink.value = !blink.value
                }

            }



        }




}