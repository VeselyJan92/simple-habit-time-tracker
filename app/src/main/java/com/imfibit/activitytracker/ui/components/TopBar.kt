package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.TestTag


@Composable
fun SimpleTopBar(
    title: String,
    endIcon: @Composable () -> Unit = { },
    onBack: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null)
            TopBarBackButton(onBack = onBack)

        Spacer(Modifier.padding(end = 16.dp))

        TopBarTitle(title = title)

        Spacer(modifier = Modifier.weight(1f))

        endIcon()
    }
}

@Composable
fun TopBarTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Black, fontSize = 25.sp
    )
}

@Composable
fun TopBarBackButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    IconButton(
        onClick = onBack,
        modifier = modifier
            .clickable(onClick = onBack)
            .testTag(TestTag.GENERAL_BACK_BUTTON),
    ){
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
    }
}