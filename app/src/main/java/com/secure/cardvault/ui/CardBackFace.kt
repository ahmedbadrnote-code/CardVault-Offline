package com.secure.cardvault.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
    revealed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.rotationY = 180f }
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // The magnetic stripe is inset from the card edges, as on many physical cards.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp)
                .height(41.dp)
                .clip(RoundedCornerShape(1.dp))
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
                            Color.White.copy(alpha = 0.16f),
                            Offset(0f, size.height * 0.12f),
                            Offset(size.width, size.height * 0.12f),
                            1f
                        )
                        drawLine(
                            Color.Black.copy(alpha = 0.62f),
                            Offset(0f, size.height * 0.88f),
                            Offset(size.width, size.height * 0.88f),
                            1.2f
                        )
                    }
                }
                .border(0.5.dp, Color(0xFF3B3E40), RoundedCornerShape(1.dp))
        )

        // One continuous signature strip: the security code is printed on its right end.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFF8F8F5), Color(0xFFD9D9D5), Color(0xFFF1F1ED))
                    )
                )
                .drawWithCache {
                    onDrawBehind {
                        // Fine security ruling, kept subtle like printed signature stock.
                        for (i in 0..6) {
                            val y = size.height * (0.12f + i * 0.12f)
                            drawLine(
                                Color(0xFF777777).copy(alpha = 0.12f),
                                Offset(size.width * 0.025f, y),
                                Offset(size.width * 0.975f, y),
                                0.5f
                            )
                        }
                    }
                }
                .padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "AUTHORIZED SIGNATURE",
                    color = Color(0xFF555555),
                    fontSize = 6.sp,
                    letterSpacing = 0.45.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF777777).copy(alpha = 0.48f))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CVV",
                    color = Color(0xFF666666),
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (revealed) card.cvv else "•••",
                    color = Color(0xFF222222),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Keep the lower reverse intentionally quiet; real cards avoid decorative clutter.
        Spacer(modifier = Modifier.height(2.dp))
    }
}
