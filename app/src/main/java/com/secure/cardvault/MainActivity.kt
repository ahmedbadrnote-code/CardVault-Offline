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
