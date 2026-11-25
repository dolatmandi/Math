package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.domain.BeamAnalysisResult
import com.example.domain.ConcreteMixResult
import com.example.domain.RcFlexureResult
import com.example.domain.SlopeResult
import com.example.ui.formatEngineeringValue
import com.example.ui.theme.LocalCivilColors
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun BeamDiagramsCanvas(
    result: BeamAnalysisResult,
    modifier: Modifier = Modifier
) {
    val civilColors = LocalCivilColors.current
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val shearColor = civilColors.shearCurve
    val momentColor = civilColors.momentCurve

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نمودار نیروی برشی (SFD) و لنگر خمشی (BMD)",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LegendDot(color = shearColor, label = "Vmax = ${formatEngineeringValue(result.maxShearKn)} kN")
                    LegendDot(color = momentColor, label = "Mmax = ${formatEngineeringValue(result.maxMomentKnm)} kN·m")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                val padX = 16.dp.toPx()
                val usableW = size.width - padX * 2f
                val shearBaseY = size.height * 0.28f
                val momentBaseY = size.height * 0.64f
                val maxAmp = size.height * 0.22f

                // Baseline axes
                drawLine(axisColor, Offset(padX, shearBaseY), Offset(padX + usableW, shearBaseY), strokeWidth = 2f)
                drawLine(axisColor, Offset(padX, momentBaseY), Offset(padX + usableW, momentBaseY), strokeWidth = 2f)

                val points = result.diagramPoints
                if (points.isNotEmpty()) {
                    val vMax = max(1e-6, points.maxOf { abs(it.shearKn) })
                    val mMax = max(1e-6, points.maxOf { abs(it.momentKnm) })

                    // Shear path
                    val shearPath = Path().apply {
                        moveTo(padX, shearBaseY)
                        points.forEach { pt ->
                            val x = padX + pt.xRatio * usableW
                            val y = shearBaseY - (pt.shearKn / vMax).toFloat() * maxAmp
                            lineTo(x, y)
                        }
                        lineTo(padX + usableW, shearBaseY)
                        close()
                    }
                    drawPath(shearPath, color = shearColor.copy(alpha = 0.22f))
                    val shearLine = Path().apply {
                        points.forEachIndexed { i, pt ->
                            val x = padX + pt.xRatio * usableW
                            val y = shearBaseY - (pt.shearKn / vMax).toFloat() * maxAmp
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                    }
                    drawPath(shearLine, color = shearColor, style = Stroke(width = 4f))

                    // Moment path (sagging drawn downward as structural convention)
                    val momentPath = Path().apply {
                        moveTo(padX, momentBaseY)
                        points.forEach { pt ->
                            val x = padX + pt.xRatio * usableW
                            val y = momentBaseY + (pt.momentKnm / mMax).toFloat() * maxAmp
                            lineTo(x, y)
                        }
                        lineTo(padX + usableW, momentBaseY)
                        close()
                    }
                    drawPath(momentPath, color = momentColor.copy(alpha = 0.22f))
                    val momentLine = Path().apply {
                        points.forEachIndexed { i, pt ->
                            val x = padX + pt.xRatio * usableW
                            val y = momentBaseY + (pt.momentKnm / mMax).toFloat() * maxAmp
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                    }
                    drawPath(momentLine, color = momentColor, style = Stroke(width = 4f))
                }

                // Support triangles on shear axis
                val triSize = 8.dp.toPx()
                listOf(padX, padX + usableW).forEach { sx ->
                    val tri = Path().apply {
                        moveTo(sx, shearBaseY)
                        lineTo(sx - triSize * 0.7f, shearBaseY + triSize)
                        lineTo(sx + triSize * 0.7f, shearBaseY + triSize)
                        close()
                    }
                    drawPath(tri, color = axisColor)
                }
            }
        }
    }
}

@Composable
fun RcSectionCanvas(
    widthMm: Double,
    heightMm: Double,
    coverMm: Double,
    result: RcFlexureResult,
    modifier: Modifier = Modifier
) {
    val civilColors = LocalCivilColors.current
    val concreteBorder = MaterialTheme.colorScheme.primary
    val concreteFill = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val compressionFill = civilColors.warningAccent.copy(alpha = 0.28f)
    val steelColor = civilColors.steelAccent

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "شماتیک مقطع تیر و بلوک فشاری ویتنی",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(width = 120.dp, height = 140.dp)
                ) {
                    val safeW = max(100.0, widthMm)
                    val safeH = max(100.0, heightMm)
                    val scale = minOf((size.width * 0.75f) / safeW.toFloat(), (size.height * 0.85f) / safeH.toFloat())
                    val rectW = safeW.toFloat() * scale
                    val rectH = safeH.toFloat() * scale
                    val left = (size.width - rectW) / 2f
                    val top = (size.height - rectH) / 2f

                    // Concrete section
                    drawRect(
                        color = concreteFill,
                        topLeft = Offset(left, top),
                        size = Size(rectW, rectH)
                    )

                    // Whitney compression block (a)
                    val aHeight = (result.compressionBlockAMm.toFloat() * scale).coerceIn(4f, rectH * 0.85f)
                    drawRect(
                        color = compressionFill,
                        topLeft = Offset(left, top),
                        size = Size(rectW, aHeight)
                    )

                    // Neutral axis dashed line (c)
                    val cY = top + (result.neutralAxisCMm.toFloat() * scale).coerceIn(4f, rectH * 0.9f)
                    drawLine(
                        color = civilColors.warningAccent,
                        start = Offset(left - 8f, cY),
                        end = Offset(left + rectW + 8f, cY),
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )

                    // Outer border
                    drawRect(
                        color = concreteBorder,
                        topLeft = Offset(left, top),
                        size = Size(rectW, rectH),
                        style = Stroke(width = 3.5f)
                    )

                    // Stirrup inner rectangle
                    val covPx = (coverMm.toFloat() * scale).coerceIn(4f, rectW * 0.25f)
                    drawRect(
                        color = concreteBorder.copy(alpha = 0.5f),
                        topLeft = Offset(left + covPx, top + covPx),
                        size = Size((rectW - 2 * covPx).coerceAtLeast(10f), (rectH - 2 * covPx).coerceAtLeast(10f)),
                        style = Stroke(width = 2f)
                    )

                    // Tension rebars (4 dots along bottom)
                    val barY = top + rectH - covPx - 5f
                    val innerLeft = left + covPx + 8f
                    val innerRight = left + rectW - covPx - 8f
                    val barCount = 4
                    for (i in 0 until barCount) {
                        val bx = innerLeft + (innerRight - innerLeft) * (i.toFloat() / (barCount - 1))
                        drawCircle(color = steelColor, radius = 5.dp.toPx(), center = Offset(bx, barY))
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "عمق موثر (d): ${formatEngineeringValue(result.effectiveDepthMm, 1)} mm",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "بلوک فشاری (a): ${formatEngineeringValue(result.compressionBlockAMm, 1)} mm",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "تار خنثی (c): ${formatEngineeringValue(result.neutralAxisCMm, 1)} mm",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "کرنش کششی (εt): ${formatEngineeringValue(result.tensileStrainEt, 5)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (result.tensileStrainEt >= 0.005) civilColors.successAccent else civilColors.warningAccent
                    )
                }
            }
        }
    }
}

@Composable
fun ConcreteMixProportionBar(result: ConcreteMixResult, modifier: Modifier = Modifier) {
    val cementColor = MaterialTheme.colorScheme.primary
    val sandColor = MaterialTheme.colorScheme.secondary
    val gravelColor = MaterialTheme.colorScheme.tertiary

    val totalVol = (result.cementKg / 1440.0) + result.sandM3 + result.gravelM3
    val cFrac = if (totalVol > 0) ((result.cementKg / 1440.0) / totalVol).toFloat() else 0.2f
    val sFrac = if (totalVol > 0) (result.sandM3 / totalVol).toFloat() else 0.3f
    val gFrac = (1f - cFrac - sFrac).coerceAtLeast(0.1f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "نسبت حجمی اجزای خشک طرح اختلاط",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
            ) {
                val w1 = size.width * cFrac
                val w2 = size.width * sFrac
                val w3 = size.width * gFrac
                drawRect(cementColor, Offset(0f, 0f), Size(w1, size.height))
                drawRect(sandColor, Offset(w1, 0f), Size(w2, size.height))
                drawRect(gravelColor, Offset(w1 + w2, 0f), Size(w3, size.height))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendDot(cementColor, "سیمان")
                LegendDot(sandColor, "ماسه")
                LegendDot(gravelColor, "شن")
            }
        }
    }
}

@Composable
fun SlopeTriangleCanvas(result: SlopeResult, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.secondary
    val fillColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "ترسیم شماتیک شیب مسیر",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val padX = 24.dp.toPx()
                val padY = 18.dp.toPx()
                val baseW = size.width - padX * 2f
                val maxH = size.height - padY * 2f
                val ratio = abs(result.verticalM / result.horizontalM).toFloat().coerceIn(0.08f, 0.85f)
                val riseH = (baseW * ratio).coerceAtMost(maxH)

                val pLeft = Offset(padX, size.height - padY)
                val pRight = Offset(padX + baseW, size.height - padY)
                val pTop = Offset(padX + baseW, size.height - padY - riseH)

                val tri = Path().apply {
                    moveTo(pLeft.x, pLeft.y)
                    lineTo(pRight.x, pRight.y)
                    lineTo(pTop.x, pTop.y)
                    close()
                }
                drawPath(tri, fillColor)
                drawLine(lineColor, pLeft, pRight, strokeWidth = 4f)
                drawLine(accentColor, pRight, pTop, strokeWidth = 4f)
                drawLine(lineColor, pLeft, pTop, strokeWidth = 5f)
            }
        }
    }
}

@Composable
fun AzimuthCompassCanvas(azimuthDeg: Double, modifier: Modifier = Modifier) {
    val ringColor = MaterialTheme.colorScheme.outline
    val northColor = MaterialTheme.colorScheme.primary
    val arrowColor = MaterialTheme.colorScheme.secondary

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Canvas(modifier = Modifier.size(110.dp)) {
                val r = size.minDimension * 0.42f
                val center = Offset(size.width / 2f, size.height / 2f)

                drawCircle(color = ringColor.copy(alpha = 0.4f), radius = r, center = center, style = Stroke(3f))
                drawLine(ringColor.copy(alpha = 0.4f), Offset(center.x - r, center.y), Offset(center.x + r, center.y), 2f)
                drawLine(northColor.copy(alpha = 0.6f), Offset(center.x, center.y - r), Offset(center.x, center.y + r), 2f)

                val rad = (azimuthDeg - 90.0) * PI / 180.0
                val tip = Offset(
                    center.x + (r * 0.85f * cos(rad)).toFloat(),
                    center.y + (r * 0.85f * sin(rad)).toFloat()
                )
                drawLine(arrowColor, center, tip, strokeWidth = 6f)
                drawCircle(color = arrowColor, radius = 5.dp.toPx(), center = tip)
                drawCircle(color = northColor, radius = 4.dp.toPx(), center = center)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "جهت‌یابی آزیموت (N = 0°)",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "گرای محاسبه‌شده ساعتگرد از شمال جغرافیایی (محور +N) اندازه‌گیری می‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
