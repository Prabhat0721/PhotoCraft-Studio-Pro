package com.prabhat.photocraftstudio.feature.editor

import android.graphics.ColorMatrix as AndroidColorMatrix
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.pow

enum class PhotoFilter(val label: String) {
    Original("Original"),
    Mono("Mono"),
    Warm("Warm"),
    Cool("Cool"),
    Vintage("Vintage"),
}

enum class LightAdjustment(val label: String) {
    Exposure("Exposure"),
    Brightness("Brightness"),
    Contrast("Contrast"),
    Highlights("Highlights"),
    Shadows("Shadows"),
    Whites("Whites"),
    Blacks("Blacks"),
}

data class LightAdjustments(
    val exposure: Float = 0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val highlights: Float = 0f,
    val shadows: Float = 0f,
    val whites: Float = 0f,
    val blacks: Float = 0f,
) {
    val isEdited: Boolean
        get() = exposure != 0f ||
            brightness != 0f ||
            contrast != 0f ||
            highlights != 0f ||
            shadows != 0f ||
            whites != 0f ||
            blacks != 0f

    fun valueFor(adjustment: LightAdjustment): Float = when (adjustment) {
        LightAdjustment.Exposure -> exposure
        LightAdjustment.Brightness -> brightness
        LightAdjustment.Contrast -> contrast
        LightAdjustment.Highlights -> highlights
        LightAdjustment.Shadows -> shadows
        LightAdjustment.Whites -> whites
        LightAdjustment.Blacks -> blacks
    }

    fun withValue(adjustment: LightAdjustment, value: Float): LightAdjustments = when (adjustment) {
        LightAdjustment.Exposure -> copy(exposure = value)
        LightAdjustment.Brightness -> copy(brightness = value)
        LightAdjustment.Contrast -> copy(contrast = value)
        LightAdjustment.Highlights -> copy(highlights = value)
        LightAdjustment.Shadows -> copy(shadows = value)
        LightAdjustment.Whites -> copy(whites = value)
        LightAdjustment.Blacks -> copy(blacks = value)
    }
}

enum class ColorAdjustment(val label: String) {
    Temperature("Temperature"),
    Tint("Tint"),
    Saturation("Saturation"),
    Vibrance("Vibrance"),
}

data class ColorAdjustments(
    val temperature: Float = 0f,
    val tint: Float = 0f,
    val saturation: Float = 0f,
    val vibrance: Float = 0f,
) {
    val isEdited: Boolean
        get() = temperature != 0f ||
            tint != 0f ||
            saturation != 0f ||
            vibrance != 0f

    fun valueFor(adjustment: ColorAdjustment): Float = when (adjustment) {
        ColorAdjustment.Temperature -> temperature
        ColorAdjustment.Tint -> tint
        ColorAdjustment.Saturation -> saturation
        ColorAdjustment.Vibrance -> vibrance
    }

    fun withValue(adjustment: ColorAdjustment, value: Float): ColorAdjustments = when (adjustment) {
        ColorAdjustment.Temperature -> copy(temperature = value)
        ColorAdjustment.Tint -> copy(tint = value)
        ColorAdjustment.Saturation -> copy(saturation = value)
        ColorAdjustment.Vibrance -> copy(vibrance = value)
    }
}

enum class EffectAdjustment(val label: String) {
    Texture("Texture"),
    Clarity("Clarity"),
    Dehaze("Dehaze"),
    Vignette("Vignette"),
    Grain("Grain"),
}

data class EffectAdjustments(
    val texture: Float = 0f,
    val clarity: Float = 0f,
    val dehaze: Float = 0f,
    val vignette: Float = 0f,
    val grain: Float = 0f,
) {
    val isEdited: Boolean
        get() = texture != 0f ||
            clarity != 0f ||
            dehaze != 0f ||
            vignette != 0f ||
            grain != 0f

    fun valueFor(adjustment: EffectAdjustment): Float = when (adjustment) {
        EffectAdjustment.Texture -> texture
        EffectAdjustment.Clarity -> clarity
        EffectAdjustment.Dehaze -> dehaze
        EffectAdjustment.Vignette -> vignette
        EffectAdjustment.Grain -> grain
    }

    fun withValue(adjustment: EffectAdjustment, value: Float): EffectAdjustments =
        when (adjustment) {
            EffectAdjustment.Texture -> copy(texture = value)
            EffectAdjustment.Clarity -> copy(clarity = value)
            EffectAdjustment.Dehaze -> copy(dehaze = value)
            EffectAdjustment.Vignette -> copy(vignette = value)
            EffectAdjustment.Grain -> copy(grain = value)
        }
}

enum class DetailAdjustment(val label: String) {
    Sharpening("Sharpening"),
    NoiseReduction("Noise reduction"),
}

data class DetailAdjustments(
    val sharpening: Float = 0f,
    val noiseReduction: Float = 0f,
) {
    val isEdited: Boolean
        get() = sharpening != 0f || noiseReduction != 0f

    fun valueFor(adjustment: DetailAdjustment): Float = when (adjustment) {
        DetailAdjustment.Sharpening -> sharpening
        DetailAdjustment.NoiseReduction -> noiseReduction
    }

    fun withValue(adjustment: DetailAdjustment, value: Float): DetailAdjustments =
        when (adjustment) {
            DetailAdjustment.Sharpening -> copy(sharpening = value)
            DetailAdjustment.NoiseReduction -> copy(noiseReduction = value)
        }
}

enum class HslChannel(val label: String, val hue: Float) {
    Red("Red", 0f),
    Orange("Orange", 30f),
    Yellow("Yellow", 60f),
    Green("Green", 120f),
    Aqua("Aqua", 180f),
    Blue("Blue", 235f),
    Purple("Purple", 275f),
    Magenta("Magenta", 320f),
}

enum class HslComponent(val label: String) {
    Hue("Hue"),
    Saturation("Saturation"),
    Luminance("Luminance"),
}

data class HslChannelAdjustment(
    val hue: Float = 0f,
    val saturation: Float = 0f,
    val luminance: Float = 0f,
) {
    val isEdited: Boolean
        get() = hue != 0f || saturation != 0f || luminance != 0f

    fun valueFor(component: HslComponent): Float = when (component) {
        HslComponent.Hue -> hue
        HslComponent.Saturation -> saturation
        HslComponent.Luminance -> luminance
    }

    fun withValue(component: HslComponent, value: Float): HslChannelAdjustment = when (component) {
        HslComponent.Hue -> copy(hue = value)
        HslComponent.Saturation -> copy(saturation = value)
        HslComponent.Luminance -> copy(luminance = value)
    }
}

data class HslAdjustments(
    val channels: Map<HslChannel, HslChannelAdjustment> = emptyMap(),
) {
    val isEdited: Boolean
        get() = channels.values.any { it.isEdited }

    fun valueFor(channel: HslChannel): HslChannelAdjustment =
        channels[channel] ?: HslChannelAdjustment()

    fun withValue(
        channel: HslChannel,
        component: HslComponent,
        value: Float,
    ): HslAdjustments {
        val next = valueFor(channel).withValue(component, value)
        return copy(
            channels = if (next == HslChannelAdjustment()) {
                channels - channel
            } else {
                channels + (channel to next)
            },
        )
    }
}

enum class GradingScope(val label: String) {
    Shadows("Shadows"),
    Midtones("Midtones"),
    Highlights("Highlights"),
    Global("Global"),
}

data class ColorWheelValue(
    val hue: Float = 0f,
    val saturation: Float = 0f,
    val luminance: Float = 0f,
) {
    val isEdited: Boolean
        get() = saturation > 0.001f || luminance != 0f

    fun withHueSaturation(hue: Float, saturation: Float): ColorWheelValue {
        val normalizedHue = ((hue % 360f) + 360f) % 360f
        return copy(
            hue = normalizedHue,
            saturation = saturation.coerceIn(0f, 1f),
        )
    }

    fun withLuminance(luminance: Float): ColorWheelValue =
        copy(luminance = luminance.coerceIn(-1f, 1f))
}

data class ColorGradingAdjustments(
    val wheels: Map<GradingScope, ColorWheelValue> = emptyMap(),
    val blend: Float = 1f,
) {
    val isEdited: Boolean
        get() = wheels.values.any { it.isEdited }

    fun valueFor(scope: GradingScope): ColorWheelValue =
        wheels[scope] ?: ColorWheelValue()

    fun withHueSaturation(
        scope: GradingScope,
        hue: Float,
        saturation: Float,
    ): ColorGradingAdjustments {
        val next = valueFor(scope).withHueSaturation(hue, saturation)
        return copy(wheels = if (next.isEdited) wheels + (scope to next) else wheels - scope)
    }

    fun withLuminance(scope: GradingScope, luminance: Float): ColorGradingAdjustments {
        val next = valueFor(scope).withLuminance(luminance)
        return copy(wheels = if (next.isEdited) wheels + (scope to next) else wheels - scope)
    }
}

data class RgbCurvePoint(
    val input: Float,
    val output: Float,
)

data class RgbChannelCurve(
    val points: List<RgbCurvePoint> = listOf(
        RgbCurvePoint(0f, 0f),
        RgbCurvePoint(1f, 1f),
    ),
) {
    val isEdited: Boolean
        get() = points != listOf(RgbCurvePoint(0f, 0f), RgbCurvePoint(1f, 1f))

    fun valueAt(x: Float): Float {
        val sorted = points.sortedBy { it.input }
        val first = sorted.firstOrNull() ?: return 0f
        val last = sorted.lastOrNull() ?: return first.output
        if (x <= first.input) return first.output
        if (x >= last.input) return last.output
        for (i in sorted.indices) {
            if (i == sorted.lastIndex) break
            val a = sorted[i]
            val b = sorted[i + 1]
            if (x in a.input..b.input) {
                val t = if (b.input == a.input) 0f else (x - a.input) / (b.input - a.input)
                return a.output + (b.output - a.output) * t
            }
        }
        return last.output
    }

    fun withValue(x: Float, y: Float): RgbChannelCurve {
        val next = mutableListOf<RgbCurvePoint>()
        var replaced = false
        for (point in points) {
            if (!replaced && point.input == x) {
                next.add(RgbCurvePoint(x, y))
                replaced = true
            } else if (point.input != x) {
                next.add(point)
            }
        }
        if (!replaced) next.add(RgbCurvePoint(x, y))
        return copy(points = next)
    }
}

enum class RgbChannel { Red, Green, Blue, Master }

data class RgbCurves(
    val red: RgbChannelCurve = RgbChannelCurve(),
    val green: RgbChannelCurve = RgbChannelCurve(),
    val blue: RgbChannelCurve = RgbChannelCurve(),
    val master: RgbChannelCurve = RgbChannelCurve(),
) {
    val isEdited: Boolean
        get() = red.isEdited || green.isEdited || blue.isEdited || master.isEdited

    fun valueAt(channel: RgbChannel, x: Float): Float = when (channel) {
        RgbChannel.Red -> red.valueAt(x)
        RgbChannel.Green -> green.valueAt(x)
        RgbChannel.Blue -> blue.valueAt(x)
        RgbChannel.Master -> master.valueAt(x)
    }

    fun withValue(channel: RgbChannel, x: Float, y: Float): RgbCurves = when (channel) {
        RgbChannel.Red -> copy(red = red.withValue(x, y))
        RgbChannel.Green -> copy(green = green.withValue(x, y))
        RgbChannel.Blue -> copy(blue = blue.withValue(x, y))
        RgbChannel.Master -> copy(master = master.withValue(x, y))
    }
}

data class EditState(
    val light: LightAdjustments = LightAdjustments(),
    val color: ColorAdjustments = ColorAdjustments(),
    val hsl: HslAdjustments = HslAdjustments(),
    val grading: ColorGradingAdjustments = ColorGradingAdjustments(),
    val effects: EffectAdjustments = EffectAdjustments(),
    val detail: DetailAdjustments = DetailAdjustments(),
    val curves: RgbCurves = RgbCurves(),
    val filter: PhotoFilter = PhotoFilter.Original,
    val rotationQuarterTurns: Int = 0,
    val flipHorizontal: Boolean = false,
) {
    val isEdited: Boolean
        get() = light.isEdited ||
            color.isEdited ||
            hsl.isEdited ||
            grading.isEdited ||
            effects.isEdited ||
            detail.isEdited ||
            curves.isEdited ||
            filter != PhotoFilter.Original ||
            rotationQuarterTurns != 0 ||
            flipHorizontal

    fun toColorMatrixValues(): FloatArray = chain(
        exposureMatrix(light.exposure),
        brightnessContrastMatrix(light.brightness, light.contrast),
        tonalRangeMatrix(light.highlights, light.shadows, light.whites, light.blacks),
        temperatureTintMatrix(color.temperature, color.tint),
        saturationVibranceMatrix(color.saturation, color.vibrance),
        hslMatrix(),
        effectsMatrix(),
        detailMatrix(),
        gradingMatrix(),
        filter.matrix(),
    )

    fun toComposeColorMatrix(): ColorMatrix = ColorMatrix(toColorMatrixValues())

    fun toAndroidColorMatrix(): AndroidColorMatrix = AndroidColorMatrix(toColorMatrixValues())

    private fun hslMatrix(): FloatArray {
        var result = identityMatrix.copyOf()
        var applied = false
        for (channel in HslChannel.entries) {
            val adjustment = hsl.valueFor(channel)
            if (!adjustment.isEdited) continue
            if (adjustment.hue != 0f) {
                result = multiply(result, hueMatrix(adjustment.hue))
            }
            result = multiply(result, saturationVibranceMatrix(adjustment.saturation, 0f))
            result = multiply(result, brightnessContrastMatrix(adjustment.luminance, 0f))
            applied = true
        }
        return if (applied) result else identityMatrix.copyOf()
    }

    private fun effectsMatrix(): FloatArray {
        var result = identityMatrix.copyOf()
        var applied = false
        if (effects.texture != 0f) {
            result = multiply(result, brightnessContrastMatrix(effects.texture, 0f))
            applied = true
        }
        if (effects.clarity != 0f) {
            result = multiply(result, brightnessContrastMatrix(0f, effects.clarity))
            applied = true
        }
        if (effects.dehaze != 0f) {
            result = multiply(result, brightnessContrastMatrix(effects.dehaze, effects.dehaze))
            applied = true
        }
        return if (applied) result else identityMatrix.copyOf()
    }

    private fun detailMatrix(): FloatArray {
        val detailAdjustments = detail
        if (detailAdjustments.sharpening == 0f && detailAdjustments.noiseReduction == 0f) {
            return identityMatrix.copyOf()
        }
        var result = identityMatrix.copyOf()
        if (detailAdjustments.sharpening != 0f) {
            val strength = detailAdjustments.sharpening.coerceIn(0.05f, 2.5f)
            result = multiply(result, floatArrayOf(
                strength * 5f, -strength, -strength, 0f, 0f,
                strength * 5f, -strength, -strength, 0f, 0f,
                strength * 5f, -strength, -strength, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            ))
        }
        if (detailAdjustments.noiseReduction != 0f) {
            val strength = detailAdjustments.noiseReduction.coerceIn(0.05f, 2f)
            result = multiply(result, floatArrayOf(
                strength / 3f, strength / 3f, strength / 3f, 0f, strength / 3f,
                strength / 3f, strength / 3f, strength / 3f, 0f, strength / 3f,
                strength / 3f, strength / 3f, strength / 3f, 0f, strength / 3f,
                0f, 0f, 0f, 1f, 0f,
            ))
        }
        return result
    }

    private fun gradingMatrix(): FloatArray {
        val scopes = listOf(
            GradingScope.Shadows,
            GradingScope.Midtones,
            GradingScope.Highlights,
            GradingScope.Global,
        )
        var result = identityMatrix.copyOf()
        var applied = false
        for (scope in scopes) {
            val value = grading.valueFor(scope)
            if (!value.isEdited) continue
            result = multiply(result, globalGradingMatrix(value))
            applied = true
        }
        if (!applied) return identityMatrix.copyOf()
        val blendValue = grading.blend.coerceIn(0f, 1f)
        if (blendValue >= 1f) return result
        return FloatArray(20) { index ->
            val row = index / 5
            val column = index % 5
            val diagonal = if (row == column && row < 4) 1f else 0f
            blendValue * result[index] + (1f - blendValue) * diagonal
        }
    }
}

private val identityMatrix = floatArrayOf(
    1f, 0f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f, 0f,
    0f, 0f, 1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f,
)

private fun hueMatrix(hueDegrees: Float): FloatArray {
    val theta = (hueDegrees * (kotlin.math.PI / 180f)).toFloat()
    val cosTheta = kotlin.math.cos(theta)
    val sinTheta = kotlin.math.sin(theta)
    val a = 1f / kotlin.math.sqrt(3f)
    val oneMinusCos = 1f - cosTheta
    val third = oneMinusCos / 3f
    return floatArrayOf(
        cosTheta + third, third - sinTheta * a, third + sinTheta * a, 0f, 0f,
        third + sinTheta * a, cosTheta + third, third - sinTheta * a, third + sinTheta * a, 0f,
        third - sinTheta * a, third + sinTheta * a, cosTheta + third, third - sinTheta * a, third + sinTheta * a,
        0f, 0f, 0f, 1f, 0f,
    )
}

private fun exposureMatrix(exposure: Float): FloatArray {
    val factor = 2f.pow(exposure * 1.5f).coerceIn(0.25f, 4f)
    return scaleMatrix(factor, factor, factor)
}

private fun brightnessContrastMatrix(brightness: Float, contrast: Float): FloatArray {
    val contrastFactor = (1f + contrast * 0.6f).coerceIn(0.35f, 1.8f)
    val offset = brightness * 90f + 128f * (1f - contrastFactor)
    return floatArrayOf(
        contrastFactor, 0f, 0f, 0f, offset,
        0f, contrastFactor, 0f, 0f, offset,
        0f, 0f, contrastFactor, 0f, offset,
        0f, 0f, 0f, 1f, 0f,
    )
}

private fun tonalRangeMatrix(
    highlights: Float,
    shadows: Float,
    whites: Float,
    blacks: Float,
): FloatArray {
    val scale = (
        1f +
            highlights * 0.10f +
            whites * 0.12f +
            shadows * 0.04f -
            blacks * 0.03f
        ).coerceIn(0.55f, 1.55f)
    val offset = -highlights * 14f + shadows * 24f + whites * 5f + blacks * 18f
    return floatArrayOf(
        scale, 0f, 0f, 0f, offset,
        0f, scale, 0f, 0f, offset,
        0f, 0f, scale, 0f, offset,
        0f, 0f, 0f, 1f, 0f,
    )
}

private fun temperatureTintMatrix(temperature: Float, tint: Float): FloatArray {
    val red = (1f + temperature * 0.18f + tint * 0.05f).coerceIn(0.6f, 1.4f)
    val green = (1f - tint * 0.12f).coerceIn(0.6f, 1.4f)
    val blue = (1f - temperature * 0.18f + tint * 0.05f).coerceIn(0.6f, 1.4f)
    return scaleMatrix(red, green, blue)
}

private fun saturationVibranceMatrix(saturation: Float, vibrance: Float): FloatArray {
    val factor = (1f + saturation * 0.9f + vibrance * 0.6f).coerceIn(0f, 2.5f)
    val inverse = 1f - factor
    val red = 0.213f * inverse
    val green = 0.715f * inverse
    val blue = 0.072f * inverse
    return floatArrayOf(
        red + factor, green, blue, 0f, 0f,
        red, green + factor, blue, 0f, 0f,
        red, green, blue + factor, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )
}

private fun globalGradingMatrix(value: ColorWheelValue): FloatArray {
    if (!value.isEdited) return identityMatrix.copyOf()

    val tint = Color.hsv(value.hue, 1f, 1f)
    val strength = value.saturation * 55f
    val luminance = value.luminance * 40f
    return floatArrayOf(
        1f, 0f, 0f, 0f, (tint.red - 0.5f) * strength + luminance,
        0f, 1f, 0f, 0f, (tint.green - 0.5f) * strength + luminance,
        0f, 0f, 1f, 0f, (tint.blue - 0.5f) * strength + luminance,
        0f, 0f, 0f, 1f, 0f,
    )
}

private fun scaleMatrix(red: Float, green: Float, blue: Float): FloatArray = floatArrayOf(
    red, 0f, 0f, 0f, 0f,
    0f, green, 0f, 0f, 0f,
    0f, 0f, blue, 0f, 0f,
    0f, 0f, 0f, 1f, 0f,
)

private fun PhotoFilter.matrix(): FloatArray = when (this) {
    PhotoFilter.Original -> identityMatrix.copyOf()

    PhotoFilter.Mono -> floatArrayOf(
        1.08f, 0f, 0f, 0f, -8f,
        0f, 1.08f, 0f, 0f, -8f,
        0f, 0f, 1.08f, 0f, -8f,
        0f, 0f, 0f, 1f, 0f,
    )

    PhotoFilter.Warm -> floatArrayOf(
        1.10f, 0f, 0f, 0f, 6f,
        0f, 1.02f, 0f, 0f, 0f,
        0f, 0f, 0.90f, 0f, -4f,
        0f, 0f, 0f, 1f, 0f,
    )

    PhotoFilter.Cool -> floatArrayOf(
        0.92f, 0f, 0f, 0f, -4f,
        0f, 1.00f, 0f, 0f, 0f,
        0f, 0f, 1.12f, 0f, 6f,
        0f, 0f, 0f, 1f, 0f,
    )

    PhotoFilter.Vintage -> {
        val factor = 0.62f
        val inverse = 1f - factor
        val red = 0.213f * inverse
        val green = 0.715f * inverse
        val blue = 0.072f * inverse
        floatArrayOf(
            (red + factor) * 1.06f, green * 1.06f, blue * 1.06f, 0f, 12f,
            red, green + factor, blue, 0f, 8f,
            red * 0.92f, green * 0.92f, (blue + factor) * 0.92f, 0f, 4f,
            0f, 0f, 0f, 1f, 0f,
        )
    }
}

private fun chain(vararg matrices: FloatArray): FloatArray =
    matrices.fold(identityMatrix.copyOf()) { result, matrix -> multiply(result, matrix) }

private fun multiply(a: FloatArray, b: FloatArray): FloatArray {
    val result = FloatArray(20)
    for (row in 0 until 4) {
        for (column in 0 until 4) {
            var sum = 0f
            for (k in 0 until 4) {
                sum += a[row * 5 + k] * b[k * 5 + column]
            }
            result[row * 5 + column] = sum
        }
        var translation = a[row * 5 + 4]
        for (k in 0 until 4) {
            translation += a[row * 5 + k] * b[k * 5 + 4]
        }
        result[row * 5 + 4] = translation
    }
    return result
}
