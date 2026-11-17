package com.secure.cardvault.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "AUTHORIZED ELECTRONIC USE ONLY • ENCRYPTED OFFLINE VAULT",
                color = subColor,
                fontSize = 7.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color(0xFF030303))
                    .border(0.5.dp, Color(0xFF2B2B2B))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                        .background(Color(0xFFE5E5E5))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = card.cardholderName.uppercase(),
                        color = Color(0xFF333333),
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                        .background(CrispWhite)
                        .border(1.dp, PitchBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (revealed) card.cvv else "•••",
                        color = PitchBlack,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            SecurityHologramSticker(accentColor = accentColor)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            color = contentColor.copy(alpha = 0.08f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.7.dp, contentColor.copy(alpha = 0.22f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CVV: ${if (revealed) card.cvv else "•••"}",
                        color = contentColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ATM PIN: ${
                            if (card.atmPin.isBlank()) "N/A"
                            else if (revealed) card.atmPin
                            else "••••"
                        }",
                        color = contentColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (card.iban.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "IBAN: ${if (revealed) card.iban else "•••• •••• " + card.iban.takeLast(4)}",
                        color = subColor,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (card.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "NOTE: ${if (revealed) card.notes else "••••••••••••"}",
                        color = subColor,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${card.bankName.uppercase()} • AES-256 SQLCIPHER",
                color = subColor,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "TAP TO FLIP FRONT ↺",
                color = contentColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
