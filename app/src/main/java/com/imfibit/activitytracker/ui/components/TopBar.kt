package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.TestTag


@Composable
fun SimpleTopBar(
    navHostController: NavHostController,
    title: String,
    backButton:Boolean = true,
    endIcon: @Composable ()->Unit = { }
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (backButton)
            TopBarBackButton(navHostController = navHostController)

        TopBarTitle(title = title)

        Spacer(modifier = Modifier.weight(1f))

        endIcon()
    }

}

@Composable
fun TopBarTitle(title: String){
    Text(
        text = title,
        fontWeight = FontWeight.Black, fontSize = 25.sp
    )
}


@Composable
fun TopBarBackButton(
    navHostController: NavHostController,
    modifier: Modifier = Modifier.padding(end = 16.dp)
){
    Icon(
        contentDescription = null,
        imageVector = Icons.Default.ArrowBackIosNew,
        tint = Color.Black,
        modifier = modifier.clickable(onClick = {
            navHostController.popBackStack()
        }).testTag(TestTag.GENERAL_BACK_BUTTON)
    )
}