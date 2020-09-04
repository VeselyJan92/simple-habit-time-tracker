package com.janvesely.activitytracker.ui.activities.composable

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.material.Divider
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.activitytracker.database.entities.TrackedActivity







@Preview
@Composable
fun BaseRow() {
    val color: Color = Color.Blue
    val title: String = "tile"
    val subtitle: String = "sub title"
    val amount: Float = 10f
    val negative: Boolean = false

    Row(
        modifier = Modifier.preferredHeight(68.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        val typography = MaterialTheme.typography
        AccountIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(Modifier.preferredWidth(12.dp))
        Column(Modifier) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                Text(text = title, style = typography.body1)
            }
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(text = subtitle, style = typography.subtitle1)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (negative) "–$ " else "$ ",
                style = typography.h6,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            Text(
                text = formatAmount(
                    amount
                ),
                style = typography.h6,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.preferredWidth(16.dp))

        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Icon(
                asset = Icons.Filled.ChevronRight,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .preferredSize(24.dp)
            )
        }
    }
    RallyDivider()
}

/**
 * A vertical colored line that is used in a [BaseRow] to differentiate accounts.
 */
@Composable
private fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(modifier.preferredSize(4.dp, 36.dp).background(color = color))
}

@Composable
fun RallyDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}

fun formatAmount(amount: Float): String {
    return "xxx"
}

val Icons.Filled.ChevronRight: VectorAsset
    get() {
        if (icon != null) return icon!!
        icon = materialIcon {
            materialPath {
                moveTo(10.0f, 6.0f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12.0f)
                lineToRelative(-4.58f, 4.59f)
                lineTo(10.0f, 18.0f)
                lineToRelative(6.0f, -6.0f)
                close()
            }
        }
        return icon!!
    }

private var icon: VectorAsset? = null
