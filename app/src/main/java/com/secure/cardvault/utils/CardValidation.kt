package com.secure.cardvault.utils

// ==================== AUTO-BRAND RADAR & LUHN CHECKSUM VALIDATOR ====================

fun detectCardBrandAutomatically(digits: String): String {
    val clean = digits.filter { it.isDigit() }
    if (clean.isEmpty()) return ""
    val p2 = clean.take(2).toIntOrNull() ?: 0
    val p4 = clean.take(4).toIntOrNull() ?: 0

    return when {
        clean.startsWith("4") -> "Visa"
        clean.startsWith("5078") || clean.startsWith("5079") -> "Meeza"
        p2 in 51..55 || p4 in 2221..2720 -> "Mastercard"
        clean.startsWith("34") || clean.startsWith("37") -> "Amex"
        clean.startsWith("6011") || clean.startsWith("65") -> "Discover"
        else -> ""
    }
}

fun isValidLuhnNumber(digits: String): Boolean {
    val clean = digits.filter { it.isDigit() }
    if (clean.length < 13) return false
    var sum = 0
    var alternate = false
    for (i in clean.length - 1 downTo 0) {
        var n = clean[i] - '0'
        if (alternate) {
            n *= 2
            if (n > 9) n -= 9
        }
        sum += n
        alternate = !alternate
    }
    return (sum % 10 == 0)
}
