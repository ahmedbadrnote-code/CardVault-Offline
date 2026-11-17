package com.secure.cardvault.ui

import androidx.compose.ui.graphics.Color

internal data class CardFinishStyle(
    val name: String,
    val gradient: List<Color>,
    val foreground: Color,
    val secondary: Color,
    val rim: Color,
    val accent: Color,
    val texture: Int
)

// Twelve distinct materials and visual identities; these replace the previous finish collection.
internal val CardFinishStyles = listOf(
    CardFinishStyle("Obsidian Reserve", listOf(Color(0xFF252326), Color(0xFF09090B), Color(0xFF030304), Color(0xFF302A27)), Color(0xFFF6E8C7), Color(0xFFC7BDA8), Color(0xFFB89A62), Color(0xFFD6B56D), 0),
    CardFinishStyle("Carbon Atelier", listOf(Color(0xFF42484E), Color(0xFF20252A), Color(0xFF111519), Color(0xFF343B40)), Color(0xFFE9EEF1), Color(0xFFABB7BF), Color(0xFF687780), Color(0xFF8FA3AD), 1),
    CardFinishStyle("Platinum Frost", listOf(Color(0xFFE2E7EA), Color(0xFF9DAAB3), Color(0xFF65737D), Color(0xFFC8D0D5)), Color(0xFF101820), Color(0xFF35434C), Color(0xFFEAF0F2), Color(0xFF54717E), 2),
    CardFinishStyle("Titanium Edge", listOf(Color(0xFF9AA4AA), Color(0xFF515D65), Color(0xFF29343B), Color(0xFF78868E)), Color(0xFFF4F6F5), Color(0xFFC0C9CD), Color(0xFFBAC5C9), Color(0xFFB9D0D8), 3),
    CardFinishStyle("Midnight Sapphire", listOf(Color(0xFF233B63), Color(0xFF101D36), Color(0xFF080F1E), Color(0xFF294B76)), Color(0xFFF0F4FF), Color(0xFFB4C3DF), Color(0xFF627FAE), Color(0xFF8FB7F2), 4),
    CardFinishStyle("Emerald Sovereign", listOf(Color(0xFF1C5A43), Color(0xFF0B3024), Color(0xFF061A14), Color(0xFF247653)), Color(0xFFF4F0D9), Color(0xFFC5D4B8), Color(0xFFB39A5A), Color(0xFFD9C17C), 5),
    CardFinishStyle("Champagne Gold", listOf(Color(0xFFD7BD83), Color(0xFF8B713F), Color(0xFF4C3A20), Color(0xFFB99A5C)), Color(0xFFFFF8E8), Color(0xFFE8D8B5), Color(0xFFE5CA8A), Color(0xFFFFE5A2), 2),
    CardFinishStyle("Crimson Signature", listOf(Color(0xFF762D3B), Color(0xFF3A101B), Color(0xFF1A080E), Color(0xFF5A1C2B)), Color(0xFFFFF0EE), Color(0xFFE0B8BA), Color(0xFF9C5360), Color(0xFFE5A5A7), 1),
    CardFinishStyle("Amethyst Noir", listOf(Color(0xFF51366D), Color(0xFF281936), Color(0xFF100B19), Color(0xFF3B2851)), Color(0xFFF7F0FF), Color(0xFFD0BEDF), Color(0xFF9675B5), Color(0xFFC8A5EF), 0),
    CardFinishStyle("Copper Heritage", listOf(Color(0xFF9A6042), Color(0xFF563321), Color(0xFF29180F), Color(0xFF75452E)), Color(0xFFFFF0E2), Color(0xFFE0BFA8), Color(0xFFB47B58), Color(0xFFE2A77C), 5),
    CardFinishStyle("Arctic Prism", listOf(Color(0xFFB9D5D9), Color(0xFF648E9A), Color(0xFF294D5A), Color(0xFF8FB8C0)), Color(0xFFF5FCFF), Color(0xFFC8E1E7), Color(0xFFB9E4EC), Color(0xFF9DE4F2), 4),
    CardFinishStyle("Graphite Minimal", listOf(Color(0xFF656B70), Color(0xFF33383D), Color(0xFF171B1F), Color(0xFF4B5258)), Color(0xFFF4F5F5), Color(0xFFBFC4C7), Color(0xFF858D92), Color(0xFFC9D0D4), 3)
)

val AllCardFinishes = CardFinishStyles.map { it.name }

internal fun cardFinishStyle(name: String): CardFinishStyle =
    CardFinishStyles.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CardFinishStyles.first()
