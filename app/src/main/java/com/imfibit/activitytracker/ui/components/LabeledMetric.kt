package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.core.value
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.core.enums.MetricStatus
import java.time.LocalDateTime

val labelHeight = 13.dp
val metricHeight = 24.dp // Slightly taller for the new pill shape
val metricTextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)

data class MetricWidgetData(
    val value: ContextString,
    val label: ContextString? = null,
    val status: MetricStatus = MetricStatus.DEFAULT,
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseMetricBlock(
    metric: String,
    color: Color,
    modifier: Modifier = Modifier,
    labelOffset: Boolean = false,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    // If the color is transparent (alpha < 1f), we want the text to cleanly contrast against the white card, so we derive a darker tint for the text.
    // Enhanced darker factor for significantly better legibility on soft pastels
    val isDark = isSystemInDarkTheme()
    val textColor = if (color.alpha < 1f) {
        if (isDark) color.copy(alpha = 1f) else color.copy(alpha = 1f).darker(0.5f)
    } else Color.White

    val clickableMod = if (onClick != null || onLongClick != null) Modifier.combinedClickable(
        onClick = onClick ?: {},
        onLongClick = onLongClick
    ) else Modifier

    Surface(
        shape = RoundedCornerShape(50), // Makes them perfect little horizontal pills
        color = color,
        modifier = modifier
            .padding(top = if (labelOffset) labelHeight else 0.dp)
            .then(if (width == 0.dp) Modifier.height(metricHeight) else Modifier.size(width, metricHeight))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .then(clickableMod)
        ) {
            Text(
                text = metric,
                textAlign = TextAlign.Center,
                color = textColor,
                style = metricStyle
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabeledMetricBlock(
    metric: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color, width: Dp = 40.dp,
    onLongClick: (() -> Unit)? = null,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .width(width)
            .height(metricHeight + labelHeight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp),
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.height(labelHeight)
        )

        BaseMetricBlock(
            metric = metric,
            color = color,
            modifier = modifier.fillMaxWidth(),
            width = 0.dp,
            metricStyle = metricStyle,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetricBlock(
    data: MetricWidgetData,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    width: Dp = 40.dp,
    metricStyle: TextStyle = metricTextStyle,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val blockColor = data.status.color.copy(alpha = alpha)

    if (data.label == null) {
        BaseMetricBlock(
            metric = data.value.value(),
            color = blockColor,
            onLongClick = onLongClick,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick,
        )
    } else {
        LabeledMetricBlock(
            metric = data.value.value(),
            label = data.label.value(),
            color = blockColor,
            onLongClick = onLongClick,
            modifier = modifier,
            width = width,
            metricStyle = metricStyle,
            onClick = onClick
        )
    }
}
