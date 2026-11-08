package com.prabhat.photocraftstudio.feature.editor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import com.prabhat.photocraftstudio.core.data.PhotoItem
import com.prabhat.photocraftstudio.core.designsystem.components.loadPhotoBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.random.Random

object PhotoExporter {

    suspend fun export(
        context: Context,
        photo: PhotoItem,
        edit: EditState,
        outputUri: Uri,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = loadPhotoBitmap(context, photo.imageModel, maxDimension = 2560)
                ?: error("Unable to load the selected photo")

            val transform = Matrix().apply {
                postRotate(edit.rotationQuarterTurns * 90f)
                if (edit.flipHorizontal) {
                    postScale(-1f, 1f)
                }
            }
            val transformed = Bitmap.createBitmap(
                source,
                0,
                0,
                source.width,
                source.height,
                transform,
                true,
            )

            var output = Bitmap.createBitmap(
                transformed.width,
                transformed.height,
                Bitmap.Config.ARGB_8888,
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                colorFilter = ColorMatrixColorFilter(edit.toAndroidColorMatrix())
            }
            Canvas(output).drawBitmap(transformed, 0f, 0f, paint)
            for (mask in edit.masks) {
                if (!mask.enabled) continue
                val masked = applyLocalAdjustments(output, mask, mask.adjustments)
                if (masked !== output) {
                    output.recycle()
                    output = masked
                }
            }
            applyVignette(output, edit.effects.vignette)
            applyGrain(output, edit.effects.grain)
            applyRgbCurves(output, edit.curves)

            val stream = context.contentResolver.openOutputStream(outputUri)
                ?: error("Unable to open the export destination")
            stream.use { destination ->
                if (!output.compress(Bitmap.CompressFormat.JPEG, 94, destination)) {
                    error("Unable to encode the edited photo")
                }
            }

            if (transformed !== source) transformed.recycle()
            output.recycle()
            source.recycle()
        }
    }

    fun applyRgbCurves(target: Bitmap, curves: RgbCurves) {
        val width = target.width
        val height = target.height
        val pixels = IntArray(width * height)
        target.getPixels(pixels, 0, width * 4, 0, 0, width, height)
        for (index in pixels.indices) {
            val argb = pixels[index]
            val r = ((argb shr 16) and 0xFF) / 255f
            val g = ((argb shr 8) and 0xFF) / 255f
            val b = (argb and 0xFF) / 255f
            val redValue = curves.red.valueAt(r)
            val greenValue = curves.green.valueAt(g)
            val blueValue = curves.blue.valueAt(b)
            val finalRed = curves.master.valueAt(redValue)
            val finalGreen = curves.master.valueAt(greenValue)
            val finalBlue = curves.master.valueAt(blueValue)
            val alpha = (argb shr 24) and 0xFF
            val redInt = (finalRed * 255f).roundToInt()
            val greenInt = (finalGreen * 255f).roundToInt()
            val blueInt = (finalBlue * 255f).roundToInt()
            pixels[index] = (alpha shl 24) or (redInt shl 16) or (greenInt shl 8) or blueInt
        }
        target.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    fun applyVignette(target: Bitmap, strength: Float) {
        if (kotlin.math.abs(strength) < 0.01f) return
        val width = target.width
        val height = target.height
        val pixels = IntArray(width * height)
        target.getPixels(pixels, 0, width * 4, 0, 0, width, height)
        val centerX = (width / 2.0f).toInt()
        val centerY = (height / 2.0f).toInt()
        val maxDistance = kotlin.math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()
        for (index in pixels.indices) {
            val argb = pixels[index]
            val red = ((argb shr 16) and 0xFF) / 255f
            val green = ((argb shr 8) and 0xFF) / 255f
            val blue = (argb and 0xFF) / 255f
            val x = (index % width) / width.toFloat()
            val y = (index / width) / height.toFloat()
            val distance = kotlin.math.sqrt((x - 0.5f) * (x - 0.5f) + (y - 0.5f) * (y - 0.5f))
            val normalized = (distance / (maxDistance + 1f)).coerceIn(0f, 1f)
            val factor = (1f - strength * normalized * normalized).coerceIn(0.2f, 1f)
            val alpha = (argb shr 24) and 0xFF
            val redInt = (red * factor * 255f).roundToInt()
            val greenInt = (green * factor * 255f).roundToInt()
            val blueInt = (blue * factor * 255f).roundToInt()
            pixels[index] = (alpha shl 24) or (redInt shl 16) or (greenInt shl 8) or blueInt
        }
        target.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    fun applyGrain(target: Bitmap, strength: Float) {
        if (kotlin.math.abs(strength) < 0.01f) return
        val width = target.width
        val height = target.height
        val pixels = IntArray(width * height)
        target.getPixels(pixels, 0, width * 4, 0, 0, width, height)
        for (index in pixels.indices) {
            val argb = pixels[index]
            val red = ((argb shr 16) and 0xFF).toFloat()
            val green = ((argb shr 8) and 0xFF).toFloat()
            val blue = (argb and 0xFF).toFloat()
            val alpha = (argb shr 24) and 0xFF
            val noise = (Random.nextFloat() - 0.5f) * strength * 255f
            val redInt = (red + noise).roundToInt().coerceIn(0, 255)
            val greenInt = (green + noise * 0.8f).roundToInt().coerceIn(0, 255)
            val blueInt = (blue + noise * 0.8f).roundToInt().coerceIn(0, 255)
            pixels[index] = (alpha shl 24) or (redInt shl 16) or (greenInt shl 8) or blueInt
        }
        target.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    fun applyLocalAdjustments(source: Bitmap, mask: Mask, adjustments: MaskAdjustments): Bitmap {
        if (!mask.enabled || !adjustments.isEdited) return source
        val width = source.width
        val height = source.height
        if (width <= 0 || height <= 0) return source
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width * 4, 0, 0, width, height)
        val opacity = mask.opacity.coerceIn(0f, 1f)
        val contrastValue = adjustments.contrast * opacity
        val brightnessValue = adjustments.brightness * opacity
        val contrastFactor = (1f + contrastValue * 0.6f).coerceIn(0.35f, 1.8f)
        val offset = brightnessValue * 90f + 128f * (1f - contrastFactor)
        val saturationValue = adjustments.saturation * opacity
        val temperatureValue = adjustments.temperature * opacity
        val adjusted = IntArray(width * height)
        for (index in pixels.indices) {
            val argb = pixels[index]
            val alpha = (argb shr 24) and 0xFF
            val red = ((argb shr 16) and 0xFF) / 255f
            val green = ((argb shr 8) and 0xFF) / 255f
            val blue = (argb and 0xFF) / 255f
            val x = (index % width) / width.toFloat()
            val y = (index / width) / height.toFloat()
            val maskValue = maskAlpha(mask, x, y)
            val effectiveAlpha = if (mask.inverted) 1f - maskValue else maskValue
            val strength = if (mask.erased) 0f else effectiveAlpha * opacity
            var finalRed = red
            var finalGreen = green
            var finalBlue = blue
            if (strength > 0.02f) {
                var redValue = red * contrastFactor + offset
                var greenValue = green * contrastFactor + offset
                var blueValue = blue * contrastFactor + offset
                redValue = redValue.coerceIn(0f, 1f)
                greenValue = greenValue.coerceIn(0f, 1f)
                blueValue = blueValue.coerceIn(0f, 1f)
                val luminance = 0.299f * redValue + 0.587f * greenValue + 0.114f * blueValue
                val scaledRed = luminance + (redValue - luminance) * (1f + saturationValue * 0.9f)
                val scaledGreen = luminance + (greenValue - luminance) * (1f + saturationValue * 0.9f)
                val scaledBlue = luminance + (blueValue - luminance) * (1f + saturationValue * 0.9f)
                finalRed = (scaledRed * (1f + temperatureValue * 0.18f)).coerceIn(0f, 1f)
                finalGreen = scaledGreen.coerceIn(0f, 1f)
                finalBlue = (scaledBlue * (1f - temperatureValue * 0.18f)).coerceIn(0f, 1f)
            }
            val redInt = (finalRed * 255f).roundToInt()
            val greenInt = (finalGreen * 255f).roundToInt()
            val blueInt = (finalBlue * 255f).roundToInt()
            adjusted[index] = (alpha shl 24) or (redInt shl 16) or (greenInt shl 8) or blueInt
        }
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(adjusted, 0, width, 0, 0, width, height)
        return result
    }
}
