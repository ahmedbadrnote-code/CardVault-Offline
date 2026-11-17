package com.secure.cardvault.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.cardvault.data.CardEntity
import com.secure.cardvault.utils.isValidLuhnNumber

// Embossed number styling.
internal val EmbossedNumberTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 21.sp,
    letterSpacing = 2.sp,
    shadow = Shadow(color = Color.Black, offset = Offset(2f, 3f), blurRadius = 2f)
)

@Composable
fun UltraLuxury3DBankCard(
    card: CardEntity,
    onCopy: (String, String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    var isFlippedToBack by remember { mutableStateOf(false) }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlippedToBack) 180f else 0f,
        animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing),
        label = "Card3DFlip"
    )

    val isLuhnValid = remember(card.cardNumber) { isValidLuhnNumber(card.cardNumber) }

    val finish = remember(card.themeContrast) { cardFinishStyle(card.themeContrast) }
    val cardBrush = remember(finish) { Brush.linearGradient(finish.gradient) }
    val contentColor = finish.foreground
    val subColor = finish.secondary
    val rimColor = finish.rim

    val cleanDigits = remember(card.cardNumber) { card.cardNumber.replace(" ", "") }
    val numberGroups = remember(cleanDigits, revealed) {
        if (revealed) {
            val padded = cleanDigits.padEnd(16, '0')
            padded.chunked(4).take(4)
        } else {
            val last4 = if (cleanDigits.length >= 4) cleanDigits.takeLast(4) else cleanDigits.padEnd(4, '•')
            listOf("••••", "••••", "••••", last4)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 16f * density
                }
                .clip(RoundedCornerShape(20.dp))
                .background(cardBrush)
                .border(1.4.dp, rimColor, RoundedCornerShape(20.dp))
                .drawWithCache {
                    val patternColor = finish.foreground.copy(alpha = 0.075f)
                    val sheenColor = finish.accent.copy(alpha = 0.10f)
                    val strokeStyle = Stroke(width = 1.05f)
                    val step = size.minDimension / 9f
                    val diagonal = Path().apply {
                        moveTo(0f, size.height * 0.30f)
                        lineTo(size.width * 0.58f, 0f)
                        lineTo(size.width * 0.82f, 0f)
                        lineTo(0f, size.height * 0.72f)
                        close()
                    }
                    onDrawBehind {
                        when (finish.texture) {
                            0 -> for (r in 1..7) drawCircle(patternColor, step * r, Offset(size.width * 0.86f, size.height * 0.5f), style = strokeStyle)
                            1 -> for (i in -8..14) drawLine(patternColor, Offset(i * step, 0f), Offset(i * step - size.height, size.height), 1.1f)
                            2 -> {
                                for (i in 1..8) {
                                    drawLine(patternColor, Offset(step * i, 0f), Offset(step * i, size.height), 0.8f)
                                    drawLine(patternColor, Offset(0f, step * i), Offset(size.width, step * i), 0.8f)
                                }
                            }
                            3 -> for (x in 1..12) for (y in 1..6) drawCircle(patternColor, 1.2f, Offset(step * x * 0.72f, step * y * 0.72f))
                            4 -> drawPath(diagonal, sheenColor)
                            5 -> for (i in 1..8) drawLine(patternColor, Offset(0f, step * i), Offset(size.width, step * i), 1.0f)
                            6 -> {
                                for (x in 0..8) for (y in 0..4) {
                                    val cx = step * x * 1.05f
                                    val cy = step * y * 1.05f
                                    val hex = Path().apply {
                                        for (point in 0..5) {
                                            val angle = Math.PI / 3.0 * point
                                            val px = cx + (step * 0.34f * kotlin.math.cos(angle)).toFloat()
                                            val py = cy + (step * 0.34f * kotlin.math.sin(angle)).toFloat()
                                            if (point == 0) moveTo(px, py) else lineTo(px, py)
                                        }
                                        close()
                                    }
                                    drawPath(hex, patternColor, style = strokeStyle)
                                }
                            }
                            7 -> for (i in 1..9) drawArc(patternColor, -35f, 70f, false, Offset(size.width * 0.72f - step * i, size.height * 0.5f - step * i), Size(step * i * 2f, step * i * 2f), style = strokeStyle)
                            8 -> {
                                for (i in -8..14) {
                                    drawLine(patternColor, Offset(i * step, 0f), Offset(i * step - size.height, size.height), 0.8f)
                                    drawLine(patternColor, Offset(i * step, size.height), Offset(i * step - size.height, 0f), 0.8f)
                                }
                            }
                            9 -> {
                                for (i in 1..5) {
                                    val inset = step * i * 0.42f
                                    drawRoundRect(patternColor, Offset(inset, inset), Size(size.width - inset * 2f, size.height - inset * 2f), CornerRadius(step * 0.22f), style = strokeStyle)
                                }
                            }
                            10 -> {
                                for (i in 1..5) {
                                    val y = step * i
                                    drawLine(patternColor, Offset(0f, y), Offset(size.width * 0.25f, y), 1.1f)
                                    drawLine(patternColor, Offset(size.width * 0.25f, y), Offset(size.width * 0.34f, y + step * 0.45f), 1.1f)
                                    drawLine(patternColor, Offset(size.width * 0.34f, y + step * 0.45f), Offset(size.width, y + step * 0.45f), 1.1f)
                                    drawCircle(sheenColor, 2.2f, Offset(size.width * 0.25f, y))
                                }
                            }
                            else -> {
                                for (i in 1..5) {
                                    val inset = step * i * 0.5f
                                    drawArc(patternColor, 180f, 90f, false, Offset(inset, inset), Size(step * 2f, step * 2f), style = strokeStyle)
                                    drawArc(patternColor, 0f, 90f, false, Offset(size.width - inset - step * 2f, size.height - inset - step * 2f), Size(step * 2f, step * 2f), style = strokeStyle)
                                }
                            }
                        }
                        // Layered studio lighting, fine material grain and a restrained machined bevel.
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.19f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(size.width * 0.72f, size.height)
                            )
                        )
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    finish.accent.copy(alpha = 0.13f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.10f)
                                ),
                                start = Offset(0f, size.height * 0.18f),
                                end = Offset(size.width, size.height * 0.92f)
                            )
                        )
                        // A deterministic micro-grain gives matte and brushed finishes a tactile surface.
                        for (i in 0 until 150) {
                            val x = ((i * 73) % 997) / 997f * size.width
                            val y = ((i * 193) % 991) / 991f * size.height
                            drawCircle(
                                color = if (i % 3 == 0) Color.White.copy(alpha = 0.035f)
                                else Color.Black.copy(alpha = 0.025f),
                                radius = if (i % 5 == 0) 1.0f else 0.55f,
                                center = Offset(x, y)
                            )
                        }
                        // Fine inner bevel: brighter on the light-facing edge, darker underneath.
                        drawLine(
                            color = Color.White.copy(alpha = 0.24f),
                            start = Offset(size.width * 0.045f, size.height * 0.075f),
                            end = Offset(size.width * 0.955f, size.height * 0.075f),
                            strokeWidth = 1.15f
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.15f),
                            start = Offset(size.width * 0.035f, size.height * 0.09f),
                            end = Offset(size.width * 0.035f, size.height * 0.90f),
                            strokeWidth = 1.0f
                        )
                        drawLine(
                            color = Color.Black.copy(alpha = 0.25f),
                            start = Offset(size.width * 0.045f, size.height * 0.94f),
                            end = Offset(size.width * 0.955f, size.height * 0.94f),
                            strokeWidth = 1.25f
                        )
                        drawPath(diagonal, sheenColor.copy(alpha = 0.55f))
                        drawRoundRect(
                            color = finish.accent.copy(alpha = 0.12f),
                            topLeft = Offset(size.width * 0.018f, size.height * 0.035f),
                            size = Size(size.width * 0.964f, size.height * 0.93f),
                            cornerRadius = CornerRadius(size.minDimension * 0.055f),
                            style = Stroke(width = 1.0f)
                        )
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isFlippedToBack = !isFlippedToBack
                }
        ) {
            if (rotationY <= 90f) {
                CardFrontFace(
                    card = card,
                    contentColor = contentColor,
                    subColor = subColor,
                    isLuhnValid = isLuhnValid,
                    numberGroups = numberGroups,
                    cleanDigits = cleanDigits,
                    revealed = revealed,
                    accentColor = finish.accent
                )
            } else {
                CardBackFace(
                    card = card,
                    contentColor = contentColor,
                    subColor = subColor,
                    revealed = revealed,
                    accentColor = finish.accent
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .offset(y = (-4).dp),
            color = Color(0xFF0D0D0D),
            shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp),
            border = BorderStroke(0.8.dp, Color(0xFF282828))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isFlippedToBack = !isFlippedToBack },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip 3D",
                        tint = CrispWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFlippedToBack) "FRONT" else "FLIP 3D",
                        color = CrispWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { revealed = !revealed },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Mask",
                        tint = CrispWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (revealed) "HIDE" else "REVEAL",
                        color = CrispWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { onCopy("Card Number", card.cardNumber) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Card Number",
                        tint = CrispWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NUMBER", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                if (card.iban.isNotBlank()) {
                    TextButton(
                        onClick = { onCopy("IBAN", card.iban) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("IBAN", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (card.atmPin.isNotBlank()) {
                    TextButton(
                        onClick = { onCopy("ATM PIN", card.atmPin) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("PIN", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Card",
                            tint = CrispWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Card",
                            tint = MutedWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== ADD & EDIT CARD DIALOG (WITH 12 METALLIC FINISHES) ====================

