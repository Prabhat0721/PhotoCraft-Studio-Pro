package com.prabhat.photocraftstudio.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LocalContentColor

enum class PhotoCraftGlyph {
    Undo,
    Redo,
    Compare,
    Export,
    Grid,
    Albums,
    Tune,
    Crop,
    Flip,
    Filter,
    Sparkle,
    Presets,
    Mask,
    Portrait,
    Settings,
    Heart,
    HeartFilled,
    Search,
    Close,
    Back,
    Check,
    Plus,
    Image,
    Reset,
}

@Composable
fun PhotoCraftIcon(
    glyph: PhotoCraftGlyph,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val scale = size.minDimension / 24f
        drawGlyph(glyph, tint, scale)
    }
}

private fun DrawScope.drawGlyph(glyph: PhotoCraftGlyph, color: Color, s: Float) {
    val stroke = Stroke(
        width = 1.9f * s,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
    )
    fun point(x: Float, y: Float) = Offset(x * s, y * s)

    when (glyph) {
        PhotoCraftGlyph.Undo -> drawUndo(color, s, stroke)
        PhotoCraftGlyph.Redo -> {
            withTransform({ scale(-1f, 1f, center) }) {
                drawUndo(color, s, stroke)
            }
        }

        PhotoCraftGlyph.Compare -> {
            drawRoundRect(
                color = color,
                topLeft = point(3f, 5f),
                size = Size(18f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s),
                style = stroke,
            )
            drawLine(color, point(12f, 5f), point(12f, 19f), 1.5f * s)
            drawRect(
                color = color.copy(alpha = 0.28f),
                topLeft = point(4.2f, 6.2f),
                size = Size(6.8f * s, 11.6f * s),
            )
        }

        PhotoCraftGlyph.Export -> {
            val tray = Path().apply {
                moveTo(5f * s, 15f * s)
                lineTo(5f * s, 19f * s)
                lineTo(19f * s, 19f * s)
                lineTo(19f * s, 15f * s)
            }
            drawPath(tray, color, style = stroke)
            drawLine(color, point(12f, 15f), point(12f, 3.5f), 1.9f * s)
            val head = Path().apply {
                moveTo(7.5f * s, 8f * s)
                lineTo(12f * s, 3.5f * s)
                lineTo(16.5f * s, 8f * s)
            }
            drawPath(head, color, style = stroke)
        }

        PhotoCraftGlyph.Grid -> {
            val cell = 7.2f * s
            val radius = androidx.compose.ui.geometry.CornerRadius(1.6f * s)
            listOf(Offset(3.2f, 3.2f), Offset(13.6f, 3.2f), Offset(3.2f, 13.6f), Offset(13.6f, 13.6f))
                .forEach { offset ->
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(offset.x * s, offset.y * s),
                        size = Size(cell, cell),
                        cornerRadius = radius,
                        style = stroke,
                    )
                }
        }

        PhotoCraftGlyph.Albums -> {
            drawRoundRect(
                color = color.copy(alpha = 0.55f),
                topLeft = point(7f, 3f),
                size = Size(14f * s, 13f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s),
                style = stroke,
            )
            drawRoundRect(
                color = color,
                topLeft = point(3f, 8f),
                size = Size(16f * s, 13f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s),
                style = stroke,
            )
        }

        PhotoCraftGlyph.Tune -> {
            val rows = listOf(6f to 15f, 12f to 8.5f, 18f to 17f)
            rows.forEach { (y, knobX) ->
                drawLine(color, point(3f, y), point(knobX - 2.6f, y), 1.7f * s)
                drawLine(color, point(knobX + 2.6f, y), point(21f, y), 1.7f * s)
                drawCircle(color, radius = 2.4f * s, center = point(knobX, y))
            }
        }

        PhotoCraftGlyph.Crop -> {
            val one = Path().apply {
                moveTo(7f * s, 2.5f * s)
                lineTo(7f * s, 17f * s)
                lineTo(21.5f * s, 17f * s)
            }
            val two = Path().apply {
                moveTo(2.5f * s, 7f * s)
                lineTo(17f * s, 7f * s)
                lineTo(17f * s, 21.5f * s)
            }
            drawPath(one, color, style = stroke)
            drawPath(two, color, style = stroke)
        }

        PhotoCraftGlyph.Flip -> {
            drawLine(
                color = color.copy(alpha = 0.55f),
                start = point(12f, 2.5f),
                end = point(12f, 21.5f),
                strokeWidth = 1.4f * s,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(2f * s, 2.4f * s),
                ),
            )
            val left = Path().apply {
                moveTo(9.5f * s, 6f * s)
                lineTo(3.5f * s, 12f * s)
                lineTo(9.5f * s, 18f * s)
                close()
            }
            drawPath(left, color, style = stroke)
            val right = Path().apply {
                moveTo(14.5f * s, 6f * s)
                lineTo(20.5f * s, 12f * s)
                lineTo(14.5f * s, 18f * s)
                close()
            }
            drawPath(right, color.copy(alpha = 0.42f), style = Stroke(
                width = 1.7f * s,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ))
        }

        PhotoCraftGlyph.Filter -> {
            drawCircle(color.copy(alpha = 0.8f), 5.2f * s, point(9f, 12f), style = stroke)
            drawCircle(color.copy(alpha = 0.8f), 5.2f * s, point(15f, 12f), style = stroke)
            drawCircle(color.copy(alpha = 0.8f), 5.2f * s, point(12f, 8.2f), style = stroke)
        }

        PhotoCraftGlyph.Sparkle -> {
            val star = Path().apply {
                moveTo(12f * s, 2.5f * s)
                lineTo(13.6f * s, 10.4f * s)
                lineTo(21.5f * s, 12f * s)
                lineTo(13.6f * s, 13.6f * s)
                lineTo(12f * s, 21.5f * s)
                lineTo(10.4f * s, 13.6f * s)
                lineTo(2.5f * s, 12f * s)
                lineTo(10.4f * s, 10.4f * s)
                close()
            }
            drawPath(star, color)
        }

        PhotoCraftGlyph.Presets -> {
            drawRoundRect(
                color = color.copy(alpha = 0.45f),
                topLeft = point(7f, 3f),
                size = Size(14f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.2f * s),
                style = stroke,
            )
            drawRoundRect(
                color = color,
                topLeft = point(3f, 7f),
                size = Size(14f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.2f * s),
                style = stroke,
            )
            drawCircle(color, 1.3f * s, point(13.4f, 17.6f))
        }

        PhotoCraftGlyph.Mask -> {
            drawCircle(
                color = color.copy(alpha = 0.85f),
                radius = 8.2f * s,
                center = point(12f, 12f),
                style = Stroke(
                    width = 1.6f * s,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(2.2f * s, 2.2f * s),
                    ),
                ),
            )
            drawCircle(
                color = color.copy(alpha = 0.28f),
                radius = 5.2f * s,
                center = point(12f, 12f),
            )
            drawCircle(color, 2.2f * s, point(12f, 12f), style = stroke)
        }

        PhotoCraftGlyph.Portrait -> {
            drawCircle(color, 3.4f * s, point(12f, 8.4f), style = stroke)
            val shoulders = Path().apply {
                moveTo(4.6f * s, 20f * s)
                cubicTo(5.4f * s, 15.8f * s, 8.4f * s, 13.8f * s, 12f * s, 13.8f * s)
                cubicTo(15.6f * s, 13.8f * s, 18.6f * s, 15.8f * s, 19.4f * s, 20f * s)
            }
            drawPath(shoulders, color, style = stroke)
        }

        PhotoCraftGlyph.Settings -> {
            drawCircle(color, 3.2f * s, point(12f, 12f), style = stroke)
            drawCircle(
                color = color.copy(alpha = 0.7f),
                radius = 7.2f * s,
                center = point(12f, 12f),
                style = stroke,
            )
            repeat(8) { index ->
                val angle = Math.toRadians((index * 45).toDouble())
                val inner = 7.6f
                val outer = 9.8f
                drawLine(
                    color = color,
                    start = point(
                        12f + kotlin.math.cos(angle).toFloat() * inner,
                        12f + kotlin.math.sin(angle).toFloat() * inner,
                    ),
                    end = point(
                        12f + kotlin.math.cos(angle).toFloat() * outer,
                        12f + kotlin.math.sin(angle).toFloat() * outer,
                    ),
                    strokeWidth = 1.9f * s,
                    cap = StrokeCap.Round,
                )
            }
        }

        PhotoCraftGlyph.Heart, PhotoCraftGlyph.HeartFilled -> {
            val heart = Path().apply {
                moveTo(12f * s, 20f * s)
                cubicTo(10f * s, 18f * s, 3.5f * s, 13.5f * s, 3.5f * s, 8.5f * s)
                cubicTo(3.5f * s, 5.5f * s, 5.8f * s, 3.5f * s, 8.5f * s, 3.5f * s)
                cubicTo(10.2f * s, 3.5f * s, 11.4f * s, 4.5f * s, 12f * s, 5.8f * s)
                cubicTo(12.6f * s, 4.5f * s, 13.8f * s, 3.5f * s, 15.5f * s, 3.5f * s)
                cubicTo(18.2f * s, 3.5f * s, 20.5f * s, 5.5f * s, 20.5f * s, 8.5f * s)
                cubicTo(20.5f * s, 13.5f * s, 14f * s, 18f * s, 12f * s, 20f * s)
                close()
            }
            if (glyph == PhotoCraftGlyph.HeartFilled) {
                drawPath(heart, color)
            } else {
                drawPath(heart, color, style = stroke)
            }
        }

        PhotoCraftGlyph.Search -> {
            drawCircle(color, 5.8f * s, point(10.5f, 10.5f), style = stroke)
            drawLine(color, point(15f, 15f), point(21f, 21f), 2.1f * s, cap = StrokeCap.Round)
        }

        PhotoCraftGlyph.Close -> {
            drawLine(color, point(5f, 5f), point(19f, 19f), 2f * s, cap = StrokeCap.Round)
            drawLine(color, point(19f, 5f), point(5f, 19f), 2f * s, cap = StrokeCap.Round)
        }

        PhotoCraftGlyph.Back -> {
            val path = Path().apply {
                moveTo(14.5f * s, 4.5f * s)
                lineTo(7f * s, 12f * s)
                lineTo(14.5f * s, 19.5f * s)
            }
            drawPath(path, color, style = stroke)
        }

        PhotoCraftGlyph.Check -> {
            val path = Path().apply {
                moveTo(4.5f * s, 12.5f * s)
                lineTo(9.5f * s, 17.5f * s)
                lineTo(19.5f * s, 6.5f * s)
            }
            drawPath(path, color, style = stroke)
        }

        PhotoCraftGlyph.Plus -> {
            drawLine(color, point(12f, 4.5f), point(12f, 19.5f), 2f * s, cap = StrokeCap.Round)
            drawLine(color, point(4.5f, 12f), point(19.5f, 12f), 2f * s, cap = StrokeCap.Round)
        }

        PhotoCraftGlyph.Image -> {
            drawRoundRect(
                color = color,
                topLeft = point(3f, 5f),
                size = Size(18f * s, 14f * s),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * s),
                style = stroke,
            )
            drawCircle(color, 1.5f * s, point(8f, 9.5f))
            val landscape = Path().apply {
                moveTo(4.5f * s, 17.5f * s)
                lineTo(9.5f * s, 11.5f * s)
                lineTo(13f * s, 15f * s)
                lineTo(16f * s, 12f * s)
                lineTo(19.5f * s, 16.5f * s)
            }
            drawPath(landscape, color, style = stroke)
        }

        PhotoCraftGlyph.Reset -> {
            drawArc(
                color = color,
                startAngle = 35f,
                sweepAngle = 290f,
                useCenter = false,
                topLeft = point(3f, 3f),
                size = Size(18f * s, 18f * s),
                style = stroke,
            )
            val head = Path().apply {
                moveTo(20.5f * s, 5f * s)
                lineTo(21f * s, 11.5f * s)
                lineTo(15f * s, 9f * s)
            }
            drawPath(head, color, style = stroke)
        }
    }
}

private fun DrawScope.drawUndo(color: Color, s: Float, stroke: Stroke) {
    val head = Path().apply {
        moveTo(8f * s, 5.5f * s)
        lineTo(3f * s, 10.5f * s)
        lineTo(8f * s, 15.5f * s)
    }
    drawPath(head, color, style = stroke)
    val tail = Path().apply {
        moveTo(3.4f * s, 10.2f * s)
        cubicTo(7f * s, 4f * s, 15f * s, 3.4f * s, 19f * s, 8f * s)
        cubicTo(21.6f * s, 10.9f * s, 21.5f * s, 14.6f * s, 19f * s, 18f * s)
    }
    drawPath(tail, color, style = stroke)
}
