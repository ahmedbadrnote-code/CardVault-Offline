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
fun LuxuryEmvChip(accentColor: Color = Color(0xFFB38228)) {
    val metal = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF4C9),
            accentColor.copy(alpha = 0.98f),
            Color(0xFF6F5527),
            Color(0xFFE8D49A),
            accentColor
        )
    )
    Box(
        modifier = Modifier
            .size(width = 55.dp, height = 42.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(metal)
            .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(7.dp))
            .drawWithCache {
                val w = size.width
                val h = size.height
                val line = Color(0xFF5B4827).copy(alpha = 0.78f)
                val inner = RoundedCornerShape(4.dp)
                onDrawBehind {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.34f),
                        topLeft = Offset(w * 0.055f, h * 0.08f),
                        size = Size(w * 0.89f, h * 0.84f),
                        cornerRadius = CornerRadius(5f),
                        style = Stroke(width = 1.0f)
                    )
                    drawRoundRect(
                        color = line,
                        topLeft = Offset(w * 0.30f, h * 0.20f),
                        size = Size(w * 0.40f, h * 0.60f),
                        cornerRadius = CornerRadius(5f),
                        style = Stroke(width = 1.45f)
                    )
                    drawLine(line, Offset(0f, h * 0.35f), Offset(w * 0.30f, h * 0.35f), 1.35f)
                    drawLine(line, Offset(0f, h * 0.65f), Offset(w * 0.30f, h * 0.65f), 1.35f)
                    drawLine(line, Offset(w * 0.70f, h * 0.35f), Offset(w, h * 0.35f), 1.35f)
                    drawLine(line, Offset(w * 0.70f, h * 0.65f), Offset(w, h * 0.65f), 1.35f)
                    drawLine(line, Offset(w * 0.50f, 0f), Offset(w * 0.50f, h * 0.20f), 1.35f)
                    drawLine(line, Offset(w * 0.50f, h * 0.80f), Offset(w * 0.50f, h), 1.35f)
                    drawLine(Color.White.copy(alpha = 0.52f), Offset(w * 0.12f, h * 0.12f), Offset(w * 0.88f, h * 0.12f), 0.8f)
                }
            }
    )
}

@Composable
fun ContactlessNfcWaves(tint: Color) {
    val waveColor = remember(tint) { tint.copy(alpha = 0.85f) }
    Spacer(
        modifier = Modifier
            .size(29.dp)
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
            .size(width = 55.dp, height = 35.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF6D83A8),
                        Color(0xFFE9D9F4),
                        Color(0xFF79C9C0),
                        Color(0xFFF2D58C),
                        Color(0xFF8D9ED0),
                        accentColor
                    )
                )
            )
            .border(0.9.dp, Color.White.copy(alpha = 0.82f), RoundedCornerShape(5.dp))
            .drawWithCache {
                val band = Stroke(width = 1.0f)
                onDrawBehind {
                    // Iridescent foil bands and fine security rings.
                    for (i in 0..7) {
                        val x = size.width * (i / 7f)
                        drawLine(
                            color = listOf(
                                Color.Cyan, Color.Magenta, Color.Yellow, Color.White
                            )[i % 4].copy(alpha = 0.22f),
                            start = Offset(x - size.height * 0.45f, 0f),
                            end = Offset(x + size.height * 0.45f, size.height),
                            strokeWidth = size.width * 0.055f
                        )
                    }
                    for (i in 1..4) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.34f),
                            radius = size.minDimension * (i * 0.105f),
                            center = Offset(size.width * 0.5f, size.height * 0.5f),
                            style = band
                        )
                    }
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.55f),
                        topLeft = Offset(size.width * 0.045f, size.height * 0.08f),
                        size = Size(size.width * 0.91f, size.height * 0.84f),
                        cornerRadius = CornerRadius(4f),
                        style = band
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SECURE",
            color = Color(0xFF17202B).copy(alpha = 0.86f),
            fontSize = 8.sp,
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
                Box(modifier = Modifier.width(50.dp).height(30.dp)) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
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
                    fontSize = 9.sp,
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "INFINITE",
                    color = contentColor.copy(alpha = 0.7f),
                    fontSize = 8.sp,
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
                    fontSize = 16.sp,
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
                    fontSize = 10.sp,
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
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
