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
            .padding(horizontal = 22.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Issuer identity and card category stay in the top band.
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
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = card.bankName.uppercase(),
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.5.sp,
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
                    text = card.cardType.uppercase(),
                    color = contentColor,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
        }

        // EMV chip sits on the left, around the vertical center of a landscape card.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LuxuryEmvChip(accentColor = accentColor)
            Spacer(modifier = Modifier.width(13.dp))
            ContactlessNfcWaves(tint = contentColor)
        }

        // The embossed PAN is placed below the chip, as on conventional bank cards.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            numberGroups.forEach { group ->
                Text(
                    text = group,
                    color = contentColor,
                    style = EmbossedNumberTextStyle.copy(
                        fontSize = 18.sp,
                        letterSpacing = 1.2.sp
                    ),
                    maxLines = 1
                )
            }
        }

        // Expiry and cardholder details form the lower band; the network mark anchors the right.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VALID THRU",
                        color = subColor,
                        fontSize = 7.sp,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = card.expiryDate,
                        color = contentColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = card.cardholderName.uppercase(),
                    color = contentColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LuxuryBrandMark(brand = card.brand, contentColor = contentColor)
        }
    }
}
