package com.prabhat.photocraftstudio.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftSpacing
import com.prabhat.photocraftstudio.core.designsystem.theme.photoCraftColors

@Composable
internal fun CurvesPanel(
    curves: RgbCurves,
    onCurveChange: (RgbChannel, Float, Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.photoCraftColors
    var selectedChannel by remember { mutableStateOf(RgbChannel.Master) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PhotoCraftSpacing.xs),
        ) {
            RgbChannel.entries.forEach { channel ->
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (channel == selectedChannel) colors.gold else colors.textSecondary,
                    modifier = Modifier.padding(vertical = PhotoCraftSpacing.xs),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.Black),
        )

        Text(
            text = "Curve editor for ${selectedChannel.name}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}
