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
internal fun CardFrontFace(
    card: CardEntity,
    contentColor: Color,
    subColor: Color,
    isLuhnValid: Boolean,
    numberGroups: List<String>,
    cleanDigits: String,
    revealed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = card.bankName.uppercase(),
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 2.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = contentColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(0.8.dp, contentColor.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "${card.cardType.uppercase()} • ${card.themeContrast.uppercase()}",
                    color = contentColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxuryEmvChip(accentColor = accentColor)
                Spacer(modifier = Modifier.width(14.dp))
                ContactlessNfcWaves(tint = contentColor)
            }

            Text(
                text = if (isLuhnValid) "✓ LUHN VERIFIED" else "TAP TO FLIP 3D ↻",
                color = subColor,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                numberGroups.forEach { group ->
                    Text(
                        text = group,
                        color = contentColor,
                        style = EmbossedNumberTextStyle
                    )
                }
            }
            if (cleanDigits.length >= 4) {
                Text(
                    text = cleanDigits.take(4),
                    color = subColor.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "VALID", color = subColor, fontSize = 6.sp, letterSpacing = 0.8.sp)
                            Text(text = "THRU", color = subColor, fontSize = 6.sp, letterSpacing = 0.8.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = card.expiryDate,
                            color = contentColor,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (revealed) {
                        Text(
                            text = "CVV: ${card.cvv}",
                            color = contentColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = card.cardholderName.uppercase(),
                    color = contentColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LuxuryBrandMark(brand = card.brand, contentColor = contentColor)
        }
    }
}
