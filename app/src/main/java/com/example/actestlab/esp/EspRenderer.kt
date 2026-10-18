package com.example.actestlab.esp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import com.example.actestlab.data.EspConfig
import com.example.actestlab.domain.Entity

@Composable
fun EspRenderer(
    entities: List<Entity>,
    config: EspConfig,
    modifier: Modifier = Modifier
) {
    val boxColor = Color(config.boxColorArgb).copy(alpha = config.boxAlpha)
    val textColor = Color(config.textColorArgb)
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sx = config.worldScale / 100f
            val sy = config.worldScale / 100f
            val paint = Paint().apply {
                color = textColor.toArgbInt()
                textSize = with(density) { 11.sp.toPx() }
                isAntiAlias = true
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            }

            entities.forEach { e ->
                val cx = e.worldX * sx
                val cy = e.worldY * sy
                val w = e.width * sx
                val h = e.height * sy
                val left = cx - w / 2f
                val top = cy - h / 2f
                val strokeColor = if (e.visible) boxColor else boxColor.copy(alpha = 0.35f)

                if (config.showBoxes) {
                    drawRect(
                        color = strokeColor,
                        topLeft = Offset(left, top),
                        size = Size(w, h),
                        style = Stroke(width = 2f)
                    )
                }
                if (config.showVisibility) {
                    drawCircle(
                        color = if (e.visible) Color(0xFF22C55E) else Color(0xFFEF4444),
                        radius = 3f,
                        center = Offset(left + w + 6f, top + 6f)
                    )
                }

                val labelParts = buildList {
                    if (config.showIds) add(e.id + "(" + e.type.name.first() + ")")
                    if (config.showDistance) add("%.1fm".format(e.distance))
                }
                val label = labelParts.joinToString("  ")
                if (label.isNotEmpty()) {
                    drawContext.canvas.nativeCanvas.drawText(label, left, top - 6f, paint)
                }
            }
        }
    }
}

private fun Color.toArgbInt(): Int =
    android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
