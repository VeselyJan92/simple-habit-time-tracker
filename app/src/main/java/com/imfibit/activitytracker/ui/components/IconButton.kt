package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.AppTheme


@Preview
@Composable
private fun IconButton_Preview() = AppTheme {
    IconButton(
        text = "Add",
        icon = Icons.Filled.Add,
        onClick = {}
    )
}

@Composable
fun IconButton(text: String, icon: ImageVector, onClick: ()->Unit ){
    Button(
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Favorite" // Provide a meaningful description
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = text)
    }
}