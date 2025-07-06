package com.imfibit.activitytracker.ui.components.selectors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors
import kotlin.math.ceil

@Preview
@Composable
fun ColorPickerComponent_Preview() = AppTheme {
    ColorPickerComponent(
        selected = Colors.chooseableColors[0],
        onChoose = {}
    )
}

@Composable
fun ColorPickerComponent(
    modifier: Modifier = Modifier,
    selected: Color,
    onChoose: (Color) -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            Colors.chooseableColors.forEach { color ->
                val colorBoxModifier = if (color == selected) {
                    Modifier.border(2.dp, Color.Black, RoundedCornerShape(5.dp))
                } else {
                    Modifier
                }

                Box(
                    modifier = colorBoxModifier
                        .size(50.dp)
                        .background(color, RoundedCornerShape(5.dp))
                        .clickable {
                            onChoose(color)
                        },
                )

            }
        },
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { measurable -> measurable.measure(constraints) }

            val size = placeables.first().height

            var boxes = constraints.maxWidth / size

            var space = (constraints.maxWidth - size * boxes) / (boxes - 1)

            // Check for too small padding
            if (space < 4.dp.toPx()) {
                boxes -= 1
                space = (constraints.maxWidth - size * boxes) / (boxes - 1)
            }

            val totalHeight =
                ceil(placeables.size.toDouble() / boxes).toInt() * (size + space) - space

            layout(constraints.maxWidth, totalHeight) {
                var xPosition = 0
                var yPosition = 0

                // Place children in the parent layout
                placeables.forEachIndexed { index, placeable ->

                    placeable.place(xPosition, yPosition)

                    if (index % boxes == boxes - 1 && index != 0) {
                        yPosition += size + space
                        xPosition = 0
                    } else {
                        xPosition += size + space
                    }
                }
            }
        }
    )
}