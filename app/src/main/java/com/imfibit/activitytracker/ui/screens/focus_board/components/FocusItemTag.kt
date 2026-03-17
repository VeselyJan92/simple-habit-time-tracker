package com.imfibit.activitytracker.ui.screens.focus_board.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.ui.components.darker

@Composable
fun FocusItemTag(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    name: String,
    color: Color,
    textModifier: Modifier = Modifier,
    iconAfter: ImageVector? = null,
    iconStart: ImageVector? = null,
    isSolidAlways: Boolean = false
) {
    // Determine background and text color based on selection state
    val backgroundColor = if (isSelected || isSolidAlways) color.darker(0.1f) else Color.Transparent
    val contentColor = if (isSelected || isSolidAlways) Color.White else color.darker(0.3f)
    val borderColor = if (!isSelected && !isSolidAlways) color.darker(0.2f) else Color.Transparent

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50), // Fully rounded pill
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconStart != null) {
                Icon(
                    modifier = Modifier.padding(end = 6.dp),
                    imageVector = iconStart,
                    contentDescription = "",
                    tint = contentColor
                )
            }

            Text(
                modifier = textModifier,
                text = name.uppercase(), // Tags are typically uppercase for this clean look
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = contentColor
            )

            if (iconAfter != null) {
                Icon(
                    modifier = Modifier.padding(start = 6.dp),
                    imageVector = iconAfter,
                    contentDescription = "",
                    tint = contentColor
                )
            }
        }
    }
}