package com.secure.cardvault.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.cardvault.data.CardEntity
import com.secure.cardvault.utils.detectCardBrandAutomatically
import com.secure.cardvault.utils.isValidLuhnNumber

@Composable
fun CardFormDialog(
    existingCard: CardEntity?,
    onDismiss: () -> Unit,
    onSave: (CardEntity) -> Unit
) {
    val isEditing = existingCard != null

    var bankName by remember { mutableStateOf(existingCard?.bankName ?: "") }
    var cardholderName by remember { mutableStateOf(existingCard?.cardholderName ?: "") }
    var cardNumber by remember { mutableStateOf(existingCard?.cardNumber ?: "") }
    var iban by remember { mutableStateOf(existingCard?.iban ?: "") }
    var expiryDate by remember { mutableStateOf(existingCard?.expiryDate ?: "") }
    var cvv by remember { mutableStateOf(existingCard?.cvv ?: "") }
    var atmPin by remember { mutableStateOf(existingCard?.atmPin ?: "") }
    var cardType by remember { mutableStateOf(existingCard?.cardType ?: "Credit") }
    var brand by remember { mutableStateOf(existingCard?.brand ?: "Visa") }
    var themeContrast by remember { mutableStateOf(existingCard?.themeContrast ?: "Pitch Black") }
    var notes by remember { mutableStateOf(existingCard?.notes ?: "") }

    val autoDetectedBrand = remember(cardNumber) { detectCardBrandAutomatically(cardNumber) }
    val luhnValid = remember(cardNumber) { isValidLuhnNumber(cardNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSurface,
        titleContentColor = CrispWhite,
        title = {
            Text(
                text = if (isEditing) "EDIT CARD" else "ADD NEW CARD",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name (e.g. CIB, NBE, QNB)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name") },
                    singleLine = true
                )

                Column {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { input ->
                            val clean = input.filter { c -> c.isDigit() }.take(16)
                            cardNumber = clean
                            val detected = detectCardBrandAutomatically(clean)
                            if (detected.isNotEmpty()) {
                                brand = detected
                            }
                        },
                        label = { Text("16-Digit Card Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val radarStatus = when {
                        cardNumber.isEmpty() -> "Auto-Brand & Luhn Radar Ready"
                        cardNumber.length < 13 -> {
                            val bInfo = if (autoDetectedBrand.isNotEmpty()) " • Brand: $autoDetectedBrand" else ""
                            "Digits: ${cardNumber.length}/16$bInfo"
                        }
                        luhnValid -> {
                            val bInfo = if (autoDetectedBrand.isNotEmpty()) autoDetectedBrand else brand
                            "✓ Valid Luhn Checksum • Detected: $bInfo"
                        }
                        else -> "⚠️ Checksum warning: Double-check digits (Luhn mismatch)"
                    }

                    Text(
                        text = radarStatus,
                        color = if (cardNumber.length >= 13 && luhnValid) CrispWhite else MutedWhite,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { if (it.length <= 5) expiryDate = it },
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 4) cvv = it.filter { c -> c.isDigit() } },
                        label = { Text("CVV") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = atmPin,
                        onValueChange = { if (it.length <= 6) atmPin = it.filter { c -> c.isDigit() } },
                        label = { Text("ATM PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = iban,
                    onValueChange = { iban = it.uppercase() },
                    label = { Text("IBAN (Optional)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand (Auto-filled or Custom)") },
                    singleLine = true
                )

                Text("Card Type:", color = MutedWhite, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Credit", "Debit", "Prepaid").forEach { typeOption ->
                        FilterChip(
                            selected = cardType.equals(typeOption, ignoreCase = true),
                            onClick = { cardType = typeOption },
                            label = { Text(typeOption, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Choose Card Design (12 Original Styles):", color = MutedWhite, fontSize = 11.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AllCardFinishes.chunked(2).forEach { rowFinishes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowFinishes.forEach { finishOption ->
                                FilterChip(
                                    selected = themeContrast.equals(finishOption, ignoreCase = true),
                                    onClick = { themeContrast = finishOption },
                                    label = { Text(finishOption, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Custom Notes") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bankName.isNotBlank() && cardNumber.length >= 4) {
                        onSave(
                            CardEntity(
                                id = existingCard?.id ?: 0,
                                bankName = bankName.trim(),
                                cardholderName = cardholderName.trim().ifEmpty { "CARDHOLDER" },
                                cardNumber = cardNumber,
                                iban = iban.trim(),
                                expiryDate = expiryDate.ifEmpty { "12/29" },
                                cvv = cvv.ifEmpty { "000" },
                                atmPin = atmPin.trim(),
                                cardType = cardType.ifEmpty { "Credit" },
                                brand = brand.ifEmpty { "Visa" },
                                themeContrast = themeContrast,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrispWhite, contentColor = PitchBlack)
            ) {
                Text(if (isEditing) "UPDATE CARD" else "SAVE CARD", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = MutedWhite)
            }
        }
    )
}
