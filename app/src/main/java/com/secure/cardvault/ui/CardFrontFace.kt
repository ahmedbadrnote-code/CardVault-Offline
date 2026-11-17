package com.secure.cardvault.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
    numberGroups: List<String>,
    revealed: Boolean,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Issuer and card category share a balanced top band.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(21.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = card.bankName.uppercase(),
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.25.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                color = contentColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(7.dp),
                border = BorderStroke(0.9.dp, contentColor.copy(alpha = 0.38f))
            ) {
                Text(
                    text = card.cardType.uppercase(),
                    color = contentColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.9.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }

        // Slightly larger EMV chip and contactless mark, centered in the card's left zone.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LuxuryEmvChip(accentColor = accentColor)
            Spacer(modifier = Modifier.width(14.dp))
            ContactlessNfcWaves(tint = contentColor)
        }

        // Fixed, compact gaps between the four PAN groups prevent excessive stretching.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            numberGroups.forEach { group ->
                Text(
                    text = group,
                    color = contentColor,
                    style = EmbossedNumberTextStyle.copy(
                        fontSize = 19.sp,
                        letterSpacing = 0.8.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Cardholder and expiry details use a larger, consistent type scale.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VALID THRU",
                        color = subColor,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = card.expiryDate,
                        color = contentColor,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = card.cardholderName.uppercase(),
                    color = contentColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LuxuryBrandMark(brand = card.brand, contentColor = contentColor)
        }
    }
}
