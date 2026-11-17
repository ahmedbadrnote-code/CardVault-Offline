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

// Card finish brushes and embossed number styling.
private val BrushPitchBlack = Brush.linearGradient(
    colors = listOf(Color(0xFF282828), Color(0xFF101010), Color(0xFF050505), Color(0xFF1E1E1E))
)
private val BrushCarbonGray = Brush.linearGradient(
    colors = listOf(Color(0xFF2A2E33), Color(0xFF16191D), Color(0xFF0D0F12), Color(0xFF202428))
)
private val BrushTitaniumSilver = Brush.linearGradient(
    colors = listOf(Color(0xFF8E959E), Color(0xFF5A616B), Color(0xFF3F444C), Color(0xFF747C87))
)
private val BrushNavyDark = Brush.linearGradient(
    colors = listOf(Color(0xFF1A2639), Color(0xFF0F1722), Color(0xFF080C12), Color(0xFF15202E))
)
private val BrushObsidianOnyx = Brush.linearGradient(
    colors = listOf(Color(0xFF232023), Color(0xFF121012), Color(0xFF080708), Color(0xFF1C191C))
)
private val BrushEmeraldGreen = Brush.linearGradient(
    colors = listOf(Color(0xFF133829), Color(0xFF0A2118), Color(0xFF05130D), Color(0xFF184532))
)
private val BrushRoyalGold = Brush.linearGradient(
    colors = listOf(Color(0xFF634F21), Color(0xFF3B2E11), Color(0xFF241B08), Color(0xFF7A6229))
)
private val BrushCrimsonRuby = Brush.linearGradient(
    colors = listOf(Color(0xFF4A151B), Color(0xFF2B0B0E), Color(0xFF170507), Color(0xFF5C1A22))
)
private val BrushRoyalAmethyst = Brush.linearGradient(
    colors = listOf(Color(0xFF2E1A3B), Color(0xFF1B0E24), Color(0xFF0F0714), Color(0xFF3B224C))
)
private val BrushCopperBronze = Brush.linearGradient(
    colors = listOf(Color(0xFF4F2D1F), Color(0xFF2E1910), Color(0xFF1A0D08), Color(0xFF633927))
)
private val BrushMidnightTeal = Brush.linearGradient(
    colors = listOf(Color(0xFF12353B), Color(0xFF0A1F24), Color(0xFF051114), Color(0xFF17434A))
)
private val BrushChampagneRose = Brush.linearGradient(
    colors = listOf(Color(0xFF4F3236), Color(0xFF2E1C1E), Color(0xFF1A0F10), Color(0xFF613E42))
)

private val BrushGoldChip = Brush.linearGradient(
    colors = listOf(Color(0xFFFFE596), Color(0xFFB38228), Color(0xFFFFF3C4), Color(0xFF8F6314))
)
private val BrushHologram = Brush.linearGradient(
    colors = listOf(Color(0xFF888888), Color(0xFFE8E8E8), Color(0xFF707070), Color(0xFFFFFFFF), Color(0xFF999999))
)

private val EmbossedNumberTextStyle = TextStyle(
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

    val cardBrush = remember(card.themeContrast) {
        when {
            card.themeContrast.equals("Carbon Gray", ignoreCase = true) -> BrushCarbonGray
            card.themeContrast.equals("Titanium Silver", ignoreCase = true) -> BrushTitaniumSilver
            card.themeContrast.equals("Navy Dark", ignoreCase = true) -> BrushNavyDark
            card.themeContrast.equals("Obsidian Onyx", ignoreCase = true) -> BrushObsidianOnyx
            card.themeContrast.equals("Emerald Green", ignoreCase = true) -> BrushEmeraldGreen
            card.themeContrast.equals("Royal Gold", ignoreCase = true) -> BrushRoyalGold
            card.themeContrast.equals("Crimson Ruby", ignoreCase = true) -> BrushCrimsonRuby
            card.themeContrast.equals("Royal Amethyst", ignoreCase = true) -> BrushRoyalAmethyst
            card.themeContrast.equals("Copper Bronze", ignoreCase = true) -> BrushCopperBronze
            card.themeContrast.equals("Midnight Teal", ignoreCase = true) -> BrushMidnightTeal
            card.themeContrast.equals("Champagne Rose", ignoreCase = true) -> BrushChampagneRose
            else -> BrushPitchBlack
        }
    }

    val contentColor = CrispWhite
    val subColor = Color(0xFFB0B0B0)
    val rimColor = Color(0xFF484848)

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
                    val patternColor = Color.White.copy(alpha = 0.032f)
                    val sheenColor = Color.White.copy(alpha = 0.028f)
                    val strokeStyle = Stroke(width = 1.2f)
                    val centerOffset = Offset(size.width * 0.85f, size.height * 0.50f)
                    val baseRadius = size.minDimension * 0.14f
                    val sheenPath = Path().apply {
                        moveTo(0f, size.height * 0.35f)
                        lineTo(size.width * 0.65f, 0f)
                        lineTo(size.width * 0.90f, 0f)
                        lineTo(0f, size.height * 0.65f)
                        close()
                    }
                    onDrawBehind {
                        for (r in 1..7) {
                            drawCircle(
                                color = patternColor,
                                radius = baseRadius * r,
                                center = centerOffset,
                                style = strokeStyle
                            )
                        }
                        drawPath(path = sheenPath, color = sheenColor)
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
                    revealed = revealed
                )
            } else {
                CardBackFace(
                    card = card,
                    contentColor = contentColor,
                    subColor = subColor,
                    revealed = revealed
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

