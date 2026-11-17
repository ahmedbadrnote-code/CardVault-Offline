package com.secure.cardvault.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrushGoldChip = Brush.linearGradient(
    colors = listOf(Color(0xFFFFE596), Color(0xFFB38228), Color(0xFFFFF3C4), Color(0xFF8F6314))
)
private val BrushHologram = Brush.linearGradient(
    colors = listOf(Color(0xFF888888), Color(0xFFE8E8E8), Color(0xFF707070), Color(0xFFFFFFFF), Color(0xFF999999))
)

@Composable
fun LuxuryEmvChip() {
    val lineColor = Color(0xFF593D08)
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 38.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(BrushGoldChip)
            .border(1.dp, lineColor.copy(alpha = 0.8f), RoundedCornerShape(7.dp))
            .drawWithCache {
                val w = size.width
                val h = size.height
                onDrawBehind {
                    drawRoundRect(
                        color = lineColor.copy(alpha = 0.7f),
                        topLeft = Offset(w * 0.30f, h * 0.22f),
                        size = Size(w * 0.40f, h * 0.56f),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 1.6f)
                    )
                    drawLine(lineColor, Offset(0f, h * 0.34f), Offset(w * 0.30f, h * 0.34f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(0f, h * 0.66f), Offset(w * 0.30f, h * 0.66f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.70f, h * 0.34f), Offset(w, h * 0.34f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.70f, h * 0.66f), Offset(w, h * 0.66f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h * 0.22f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.5f, h * 0.78f), Offset(w * 0.5f, h), strokeWidth = 1.4f)
                }
            }
    )
}

@Composable
fun ContactlessNfcWaves(tint: Color) {
    val waveColor = remember(tint) { tint.copy(alpha = 0.85f) }
    Spacer(
        modifier = Modifier
            .size(26.dp)
            .drawWithCache {
                val stroke = Stroke(width = 2.5f, cap = StrokeCap.Round)
                onDrawBehind {
                    for (i in 1..4) {
                        val radius = (size.minDimension / 5.2f) * i
                        drawArc(
                            color = waveColor,
                            startAngle = -40f,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = Offset(size.width * 0.1f - radius / 2, size.height / 2 - radius / 2),
                            size = Size(radius, radius),
                            style = stroke
                        )
                    }
                }
            }
    )
}

@Composable
fun SecurityHologramSticker(accentColor: Color = Color(0xFFB8C6CC)) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(BrushHologram)
            .border(0.8.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SECURE",
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun LuxuryBrandMark(brand: String, contentColor: Color) {
    val cleanBrand = remember(brand) { brand.trim().lowercase() }
    when {
        cleanBrand.contains("master") -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(44.dp).height(26.dp)) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterStart)
                            .background(contentColor.copy(alpha = 0.85f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterEnd)
                            .border(1.5.dp, contentColor, CircleShape)
                            .background(contentColor.copy(alpha = 0.35f), CircleShape)
                    )
                }
                Text(
                    text = "mastercard",
                    color = contentColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
        cleanBrand.contains("visa") -> {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "VISA",
                    color = contentColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "INFINITE",
                    color = contentColor.copy(alpha = 0.7f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
        cleanBrand.contains("meeza") -> {
            Surface(
                color = contentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.2.dp, contentColor)
            ) {
                Text(
                    text = "MEEZA",
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        cleanBrand.contains("amex") -> {
            Surface(
                color = contentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, contentColor)
            ) {
                Text(
                    text = "AMERICAN EXPRESS",
                    color = contentColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
        else -> {
            Text(
                text = brand.uppercase(),
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
