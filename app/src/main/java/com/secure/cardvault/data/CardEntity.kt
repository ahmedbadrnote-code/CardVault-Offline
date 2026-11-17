package com.secure.cardvault.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bankName: String,
    val cardholderName: String,
    val cardNumber: String,
    val iban: String,
    val expiryDate: String,
    val cvv: String,
    val atmPin: String,
    val cardType: String,
    val brand: String,
    val themeContrast: String,
    val notes: String
)
