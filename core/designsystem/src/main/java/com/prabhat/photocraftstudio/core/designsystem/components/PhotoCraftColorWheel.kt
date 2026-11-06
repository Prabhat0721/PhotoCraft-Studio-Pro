package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

private val wheelColors = listOf(
    Color.hsv(0f, 1f, 1f),
    Color.hsv(45f, 1f, 1f),
    Color.hsv(90f, 1f, 1f),
    Color.hsv(135f, 1f, 1f),
    Color.hsv(180f, 1f, 1f),
    Color.hsv(225f, 1f, 1f),
    Color.hsv(270f, 1f, 1f),
    Color.hsv(315f, 1f, 1f),
    Color.hsv(360f, 1f, 1f),
)

@Composable
fun PhotoCraftColorWheel(
    hue: Float,
    saturation: Float,
    onHueSaturationChange: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.photoCraftColors
    val currentOnChange by rememberUpdatedState(onHueSaturationChange)
    val currentOnFinished by rememberUpdatedState(onValueChangeFinished)
    val safeHue = ((hue % 360f) + 360f) % 360f
    val safeSaturation = saturation.coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .semantics {
                contentDescription = "Color wheel"
                stateDescription = "Hue ${safeHue.roundToInt()} degrees, " +
                    "saturation ${(safeSaturation * 100f).roundToInt()} percent"
            }
            .pointerInput(Unit) {
                fun updateFromPosition(position: Offset) {
                    val radius = minOf(size.width, size.height) / 2f
                    if (radius <= 0f) return
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = position.x - center.x
                    val dy = position.y - center.y
                    val nextSaturation = (hypot(dx, dy) / radius).coerceIn(0f, 1f)
                    val degrees = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                    val nextHue = if (degrees < 0f) degrees + 360f else degrees
                    currentOnChange(nextHue, nextSaturation)
                }

                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    updateFromPosition(down.position)
                    down.consume()
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        updateFromPosition(change.position)
                        change.consume()
                    }
                    currentOnFinished?.invoke()
                }
            },
    ) {
        val ringWidth = 1.dp.toPx()
        val radius = (size.minDimension - ringWidth) / 2f
        val wheelCenter = center

        drawCircle(
            brush = Brush.sweepGradient(colors = wheelColors, center = wheelCenter),
            radius = radius,
            center = wheelCenter,
        )
        drawCircle(
            brush = Brush.radialGradient(
                0f to Color.White,
                0.55f to Color.White.copy(alpha = 0.38f),
                1f to Color.Transparent,
                center = wheelCenter,
                radius = radius,
            ),
            radius = radius,
            center = wheelCenter,
        )
        drawCircle(
            color = colors.divider,
            radius = radius,
            center = wheelCenter,
            style = Stroke(width = ringWidth),
        )

        val radians = Math.toRadians(safeHue.toDouble())
        val thumbCenter = Offset(
            x = wheelCenter.x + cos(radians).toFloat() * radius * safeSaturation,
            y = wheelCenter.y + sin(radians).toFloat() * radius * safeSaturation,
        )
        val thumbColor = if (safeSaturation < 0.02f) {
            Color.White
        } else {
            Color.hsv(safeHue, safeSaturation.coerceAtLeast(0.3f), 0.95f)
        }

        drawCircle(
            color = Color.Black.copy(alpha = 0.5f),
            radius = 9.dp.toPx(),
            center = thumbCenter,
        )
        drawCircle(
            color = Color.White,
            radius = 7.dp.toPx(),
            center = thumbCenter,
        )
        drawCircle(
            color = thumbColor,
            radius = 5.dp.toPx(),
            center = thumbCenter,
        )
    }
}
