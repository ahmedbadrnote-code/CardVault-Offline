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


// ==================== HARDWARE-ACCELERATED 3D FLIPPING BANK CARD ====================

@Composable
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

