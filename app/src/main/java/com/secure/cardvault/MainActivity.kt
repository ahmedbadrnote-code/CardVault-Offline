package com.secure.cardvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.secure.cardvault.data.CardEntity
import com.secure.cardvault.data.VaultDatabase
import com.secure.cardvault.utils.detectCardBrandAutomatically
import com.secure.cardvault.utils.isValidLuhnNumber
import com.secure.cardvault.utils.copyToClipboard
import com.secure.cardvault.ui.*
import kotlinx.coroutines.launch

// 12 PRE-ALLOCATED LUXURY METALLIC BRUSHES
private val BrushPitchBlack = Brush.linearGradient(
    colors = listOf(Color(0xFF282828), Color(0xFF101010), Color(0xFF050505), Color(0xFF1E1E1E))
)
private val BrushCarbonGray = Brush.linearGradient(
    colors = listOf(Color(0xFF2A2E33), Color(0xFF16191D), Color(0xFF0D0F12), Color(0xFF202428))
)
private val BrushTitaniumSilver = Brush.linearGradient(
    colors = listOf(Color(0xFF8E959E), Color(0xFF5A616B), Color(0xFF3F444C), Color(0xFF747C87))
)
private val BrushNavyDark = Brush.linearGradient(
    colors = listOf(Color(0xFF1A2639), Color(0xFF0F1722), Color(0xFF080C12), Color(0xFF15202E))
)
private val BrushObsidianOnyx = Brush.linearGradient(
    colors = listOf(Color(0xFF232023), Color(0xFF121012), Color(0xFF080708), Color(0xFF1C191C))
)
private val BrushEmeraldGreen = Brush.linearGradient(
    colors = listOf(Color(0xFF133829), Color(0xFF0A2118), Color(0xFF05130D), Color(0xFF184532))
)
private val BrushRoyalGold = Brush.linearGradient(
    colors = listOf(Color(0xFF634F21), Color(0xFF3B2E11), Color(0xFF241B08), Color(0xFF7A6229))
)
private val BrushCrimsonRuby = Brush.linearGradient(
    colors = listOf(Color(0xFF4A151B), Color(0xFF2B0B0E), Color(0xFF170507), Color(0xFF5C1A22))
)
private val BrushRoyalAmethyst = Brush.linearGradient(
    colors = listOf(Color(0xFF2E1A3B), Color(0xFF1B0E24), Color(0xFF0F0714), Color(0xFF3B224C))
)
private val BrushCopperBronze = Brush.linearGradient(
    colors = listOf(Color(0xFF4F2D1F), Color(0xFF2E1910), Color(0xFF1A0D08), Color(0xFF633927))
)
private val BrushMidnightTeal = Brush.linearGradient(
    colors = listOf(Color(0xFF12353B), Color(0xFF0A1F24), Color(0xFF051114), Color(0xFF17434A))
)
private val BrushChampagneRose = Brush.linearGradient(
    colors = listOf(Color(0xFF4F3236), Color(0xFF2E1C1E), Color(0xFF1A0F10), Color(0xFF613E42))
)

private val BrushGoldChip = Brush.linearGradient(
    colors = listOf(Color(0xFFFFE596), Color(0xFFB38228), Color(0xFFFFF3C4), Color(0xFF8F6314))
)
private val BrushHologram = Brush.linearGradient(
    colors = listOf(Color(0xFF888888), Color(0xFFE8E8E8), Color(0xFF707070), Color(0xFFFFFFFF), Color(0xFF999999))
)

private val EmbossedNumberTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 21.sp,
    letterSpacing = 2.sp,
    shadow = Shadow(color = Color.Black, offset = Offset(2f, 3f), blurRadius = 2f)
)

val AllCardFinishes = listOf(
    "Pitch Black",
    "Carbon Gray",
    "Titanium Silver",
    "Navy Dark",
    "Obsidian Onyx",
    "Emerald Green",
    "Royal Gold",
    "Crimson Ruby",
    "Royal Amethyst",
    "Copper Bronze",
    "Midnight Teal",
    "Champagne Rose"
)

class MainActivity : AppCompatActivity() {

    private var isUnlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val db = VaultDatabase.getDatabase(applicationContext)
        val dao = db.cardDao()

        setContent {
            CardVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PitchBlack
                ) {
                    if (!isUnlocked) {
                        LockedScreen(onUnlockRequest = { promptBiometric() })
                    } else {
                        val cards by dao.getAllCards().collectAsState(initial = emptyList())
                        VaultAppScreen(
                            cards = cards,
                            onSaveOrUpdateCard = { card ->
                                lifecycleScope.launch { dao.insertOrUpdateCard(card) }
                            },
                            onDeleteCard = { card ->
                                lifecycleScope.launch { dao.deleteCard(card) }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isUnlocked) {
            promptBiometric()
        }
    }

    override fun onPause() {
        super.onPause()
        isUnlocked = false
    }

    private fun promptBiometric() {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        if (biometricManager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            isUnlocked = true
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isUnlocked = true
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("CardVault Offline")
            .setSubtitle("Authenticate to unlock your encrypted card vault")
            .setAllowedAuthenticators(authenticators)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}


@Composable
fun VaultAppScreen(
    cards: List<CardEntity>,
    onSaveOrUpdateCard: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var showCardDialog by remember { mutableStateOf(false) }
    var cardBeingEdited by remember { mutableStateOf<CardEntity?>(null) }

    Scaffold(
        containerColor = PitchBlack,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = CrispWhite,
                    contentColor = PitchBlack,
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CrispWhite)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "CARDVAULT",
                                color = CrispWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "RELEASE R8 OPTIMIZED • 12 METALLIC FINISHES",
                                color = MutedWhite,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitchBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    cardBeingEdited = null
                    showCardDialog = true
                },
                containerColor = CrispWhite,
                contentColor = PitchBlack,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = BorderGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No cards stored in offline vault", color = MutedWhite)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = cards,
                    key = { it.id },
                    contentType = { "bank_card_3d" }
                ) { card ->
                    UltraLuxury3DBankCard(
                        card = card,
                        onCopy = { label, value ->
                            copyToClipboard(context, label, value)
                            scope.launch {
                                snackbarHostState.showSnackbar("$label copied to clipboard")
                            }
                        },
                        onEdit = {
                            cardBeingEdited = card
                            showCardDialog = true
                        },
                        onDelete = { onDeleteCard(card) }
                    )
                }
            }
        }

        if (showCardDialog) {
            CardFormDialog(
                existingCard = cardBeingEdited,
                onDismiss = {
                    showCardDialog = false
                    cardBeingEdited = null
                },
                onSave = { savedCard ->
                    onSaveOrUpdateCard(savedCard)
                    val msg = if (cardBeingEdited != null) "Card updated successfully" else "Card added to vault"
                    showCardDialog = false
                    cardBeingEdited = null
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
            )
        }
    }
}

// ==================== CACHED GPU EMV CHIP, HOLOGRAM & BRAND LOGOS ====================

@Composable
fun LuxuryEmvChip() {
    val lineColor = Color(0xFF593D08)
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 38.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(BrushGoldChip)
            .border(1.dp, lineColor.copy(alpha = 0.8f), RoundedCornerShape(7.dp))
            .drawWithCache {
                val w = size.width
                val h = size.height
                onDrawBehind {
                    drawRoundRect(
                        color = lineColor.copy(alpha = 0.7f),
                        topLeft = Offset(w * 0.30f, h * 0.22f),
                        size = Size(w * 0.40f, h * 0.56f),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 1.6f)
                    )
                    drawLine(lineColor, Offset(0f, h * 0.34f), Offset(w * 0.30f, h * 0.34f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(0f, h * 0.66f), Offset(w * 0.30f, h * 0.66f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.70f, h * 0.34f), Offset(w, h * 0.34f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.70f, h * 0.66f), Offset(w, h * 0.66f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h * 0.22f), strokeWidth = 1.4f)
                    drawLine(lineColor, Offset(w * 0.5f, h * 0.78f), Offset(w * 0.5f, h), strokeWidth = 1.4f)
                }
            }
    )
}

@Composable
fun ContactlessNfcWaves(tint: Color) {
    val waveColor = remember(tint) { tint.copy(alpha = 0.85f) }
    Spacer(
        modifier = Modifier
            .size(26.dp)
            .drawWithCache {
                val stroke = Stroke(width = 2.5f, cap = StrokeCap.Round)
                onDrawBehind {
                    for (i in 1..4) {
                        val radius = (size.minDimension / 5.2f) * i
                        drawArc(
                            color = waveColor,
                            startAngle = -40f,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = Offset(size.width * 0.1f - radius / 2, size.height / 2 - radius / 2),
                            size = Size(radius, radius),
                            style = stroke
                        )
                    }
                }
            }
    )
}

@Composable
fun SecurityHologramSticker() {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(BrushHologram)
            .border(0.8.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SECURE",
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun LuxuryBrandMark(brand: String, contentColor: Color) {
    val cleanBrand = remember(brand) { brand.trim().lowercase() }
    when {
        cleanBrand.contains("master") -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.width(44.dp).height(26.dp)) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterStart)
                            .background(contentColor.copy(alpha = 0.85f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterEnd)
                            .border(1.5.dp, contentColor, CircleShape)
                            .background(contentColor.copy(alpha = 0.35f), CircleShape)
                    )
                }
                Text(
                    text = "mastercard",
                    color = contentColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
        cleanBrand.contains("visa") -> {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "VISA",
                    color = contentColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "INFINITE",
                    color = contentColor.copy(alpha = 0.7f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
        cleanBrand.contains("meeza") -> {
            Surface(
                color = contentColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.2.dp, contentColor)
            ) {
                Text(
                    text = "MEEZA",
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        cleanBrand.contains("amex") -> {
            Surface(
                color = contentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, contentColor)
            ) {
                Text(
                    text = "AMERICAN EXPRESS",
                    color = contentColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
        else -> {
            Text(
                text = brand.uppercase(),
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ==================== HARDWARE-ACCELERATED 3D FLIPPING BANK CARD ====================

@Composable
fun UltraLuxury3DBankCard(
    card: CardEntity,
    onCopy: (String, String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    var isFlippedToBack by remember { mutableStateOf(false) }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlippedToBack) 180f else 0f,
        animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing),
        label = "Card3DFlip"
    )

    val isLuhnValid = remember(card.cardNumber) { isValidLuhnNumber(card.cardNumber) }

    val cardBrush = remember(card.themeContrast) {
        when {
            card.themeContrast.equals("Carbon Gray", ignoreCase = true) -> BrushCarbonGray
            card.themeContrast.equals("Titanium Silver", ignoreCase = true) -> BrushTitaniumSilver
            card.themeContrast.equals("Navy Dark", ignoreCase = true) -> BrushNavyDark
            card.themeContrast.equals("Obsidian Onyx", ignoreCase = true) -> BrushObsidianOnyx
            card.themeContrast.equals("Emerald Green", ignoreCase = true) -> BrushEmeraldGreen
            card.themeContrast.equals("Royal Gold", ignoreCase = true) -> BrushRoyalGold
            card.themeContrast.equals("Crimson Ruby", ignoreCase = true) -> BrushCrimsonRuby
            card.themeContrast.equals("Royal Amethyst", ignoreCase = true) -> BrushRoyalAmethyst
            card.themeContrast.equals("Copper Bronze", ignoreCase = true) -> BrushCopperBronze
            card.themeContrast.equals("Midnight Teal", ignoreCase = true) -> BrushMidnightTeal
            card.themeContrast.equals("Champagne Rose", ignoreCase = true) -> BrushChampagneRose
            else -> BrushPitchBlack
        }
    }

    val contentColor = CrispWhite
    val subColor = Color(0xFFB0B0B0)
    val rimColor = Color(0xFF484848)

    val cleanDigits = remember(card.cardNumber) { card.cardNumber.replace(" ", "") }
    val numberGroups = remember(cleanDigits, revealed) {
        if (revealed) {
            val padded = cleanDigits.padEnd(16, '0')
            padded.chunked(4).take(4)
        } else {
            val last4 = if (cleanDigits.length >= 4) cleanDigits.takeLast(4) else cleanDigits.padEnd(4, '•')
            listOf("••••", "••••", "••••", last4)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 16f * density
                }
                .clip(RoundedCornerShape(20.dp))
                .background(cardBrush)
                .border(1.4.dp, rimColor, RoundedCornerShape(20.dp))
                .drawWithCache {
                    val patternColor = Color.White.copy(alpha = 0.032f)
                    val sheenColor = Color.White.copy(alpha = 0.028f)
                    val strokeStyle = Stroke(width = 1.2f)
                    val centerOffset = Offset(size.width * 0.85f, size.height * 0.50f)
                    val baseRadius = size.minDimension * 0.14f
                    val sheenPath = Path().apply {
                        moveTo(0f, size.height * 0.35f)
                        lineTo(size.width * 0.65f, 0f)
                        lineTo(size.width * 0.90f, 0f)
                        lineTo(0f, size.height * 0.65f)
                        close()
                    }
                    onDrawBehind {
                        for (r in 1..7) {
                            drawCircle(
                                color = patternColor,
                                radius = baseRadius * r,
                                center = centerOffset,
                                style = strokeStyle
                            )
                        }
                        drawPath(path = sheenPath, color = sheenColor)
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isFlippedToBack = !isFlippedToBack
                }
        ) {
            if (rotationY <= 90f) {
                // FRONT OF CARD
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
                            LuxuryEmvChip()
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
            } else {
                // BACK OF CARD
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
                        SecurityHologramSticker()
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
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .offset(y = (-4).dp),
            color = Color(0xFF0D0D0D),
            shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp),
            border = BorderStroke(0.8.dp, Color(0xFF282828))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isFlippedToBack = !isFlippedToBack },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip 3D",
                        tint = CrispWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isFlippedToBack) "FRONT" else "FLIP 3D",
                        color = CrispWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { revealed = !revealed },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Mask",
                        tint = CrispWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (revealed) "HIDE" else "REVEAL",
                        color = CrispWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { onCopy("Card Number", card.cardNumber) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Card Number",
                        tint = CrispWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NUMBER", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                if (card.iban.isNotBlank()) {
                    TextButton(
                        onClick = { onCopy("IBAN", card.iban) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("IBAN", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (card.atmPin.isNotBlank()) {
                    TextButton(
                        onClick = { onCopy("ATM PIN", card.atmPin) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("PIN", color = CrispWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Card",
                            tint = CrispWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Card",
                            tint = MutedWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== ADD & EDIT CARD DIALOG (WITH 12 METALLIC FINISHES) ====================

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

                Text("Metallic Card Finish (12 Luxury Styles):", color = MutedWhite, fontSize = 11.sp)
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

