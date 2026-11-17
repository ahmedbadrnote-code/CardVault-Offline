package com.secure.cardvault.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.cardvault.data.CardEntity

@Composable
internal fun CardBackFace(
    card: CardEntity,
    contentColor: Color,
    subColor: Color,
    revealed: Boolean,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.rotationY = 180f }
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // A realistic full-width magnetic stripe in the upper portion of the reverse.
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(39.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF08090A),
                            Color(0xFF242628),
                            Color(0xFF050607),
                            Color(0xFF17191B)
                        )
                    )
                )
                .drawWithCache {
                    onDrawBehind {
                        drawLine(
                            Color.White.copy(alpha = 0.18f),
                            Offset(0f, size.height * 0.12f),
                            Offset(size.width, size.height * 0.12f),
                            1f
                        )
                        drawLine(
                            Color.Black.copy(alpha = 0.65f),
                            Offset(0f, size.height * 0.88f),
                            Offset(size.width, size.height * 0.88f),
                            1.2f
                        )
                    }
                }
                .border(0.5.dp, Color(0xFF3B3E40))
        )

        // Signature panel, printed security code and hologram occupy the lower reverse.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(37.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFF8F8F5), Color(0xFFD9D9D5), Color(0xFFF1F1ED))
                        )
                    )
                    .drawWithCache {
                        onDrawBehind {
                            for (i in 0..7) {
                                val y = size.height * (0.10f + i * 0.11f)
                                drawLine(
                                    Color(0xFF777777).copy(alpha = 0.13f),
                                    Offset(size.width * 0.03f, y),
                                    Offset(size.width * 0.97f, y),
                                    0.55f
                                )
                            }
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "AUTHORIZED SIGNATURE",
                    color = Color(0xFF555555),
                    fontSize = 6.sp,
                    letterSpacing = 0.55.sp
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp))
            }

            Column(
                modifier = Modifier
                    .width(42.dp)
                    .height(37.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF7F7F4))
                    .border(0.7.dp, Color(0xFFB8B8B2), RoundedCornerShape(3.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (revealed) card.cvv else "•••",
                    color = Color(0xFF222222),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }

            SecurityHologramSticker(accentColor = accentColor)
        }

        // No vault metadata or implementation details are printed on the physical-card surface.
        Spacer(modifier = Modifier.height(5.dp))
    }
}
