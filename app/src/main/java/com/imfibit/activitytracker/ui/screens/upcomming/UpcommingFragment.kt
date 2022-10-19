package com.imfibit.activitytracker.ui.screens.upcomming

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.ui.components.Colors



@Composable
fun ScreenUpcoming(navControl: NavHostController, scaffoldState: ScaffoldState) {
    Scaffold(
            content = {
                Box(Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
                    Text(
                            text = "UPCOMMING FEATURE",
                            style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                            )
                    )
                }
            },
            backgroundColor = Colors . AppBackground
    )
}
