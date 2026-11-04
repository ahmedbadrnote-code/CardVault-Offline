import os

files = {
    "settings.gradle.kts": """pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "CardVaultOffline"
include(":app")
""",

    "build.gradle.kts": """plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
""",

    "gradle.properties": """org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
""",

    "app/proguard-rules.pro": """-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
""",

    "app/build.gradle.kts": """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.secure.cardvault"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.secure.cardvault"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Room Local Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // SQLCipher & Security Crypto for Full Local Encryption
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Biometric Authentication
    implementation("androidx.biometric:biometric:1.1.0")
}
""",

    "app/src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- STRICT OFFLINE MODE: NO INTERNET PERMISSION INCLUDED -->
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />

    <application
        android:allowBackup="false"
        android:fullBackupContent="false"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:label="CardVault Offline"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""",

    "app/src/main/res/xml/data_extraction_rules.xml": """<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <exclude domain="root" />
        <exclude domain="database" />
        <exclude domain="sharedpref" />
    </cloud-backup>
    <device-transfer>
        <exclude domain="root" />
        <exclude domain="database" />
        <exclude domain="sharedpref" />
    </device-transfer>
</data-extraction-rules>
""",

    "app/src/main/java/com/secure/cardvault/data/CardEntity.kt": """package com.secure.cardvault.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bankName: String,
    val cardholderName: String,
    val cardNumber: String,
    val iban: String,
    val expiryDate: String,
    val cvv: String,
    val cardType: String,     // Credit, Debit, Prepaid
    val brand: String,        // Visa, Mastercard, Amex, etc.
    val themeContrast: String,// Monochrome style: Pitch Black, Crisp White, Carbon Gray
    val billingDueDay: Int,   // 1-31
    val notes: String
)
""",

    "app/src/main/java/com/secure/cardvault/data/CardDao.kt": """package com.secure.cardvault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)
}
""",

    "app/src/main/java/com/secure/cardvault/data/VaultDatabase.kt": """package com.secure.cardvault.data

import android.content.Context
import android.util.Base64
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(entities = [CardEntity::class], version = 1, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                System.loadLibrary("sqlcipher")
                val passphrase = getOrGeneratePassphrase(context)
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VaultDatabase::class.java,
                    "cardvault_encrypted.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun getOrGeneratePassphrase(context: Context): ByteArray {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                context,
                "vault_key_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val existing = prefs.getString("db_passphrase", null)
            if (existing != null) {
                return Base64.decode(existing, Base64.NO_WRAP)
            }

            val randomBytes = ByteArray(32)
            SecureRandom().nextBytes(randomBytes)
            val encoded = Base64.encodeToString(randomBytes, Base64.NO_WRAP)
            prefs.edit().putString("db_passphrase", encoded).apply()
            return randomBytes
        }
    }
}
""",

    "app/src/main/java/com/secure/cardvault/ui/Theme.kt": """package com.secure.cardvault.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// STRICT MONOCHROME DARK MODE PALETTE (Pitch Black & Crisp White)
val PitchBlack = Color(0xFF000000)
val DeepSurface = Color(0xFF0A0A0A)
val CardGray = Color(0xFF121212)
val BorderGray = Color(0xFF333333)
val CrispWhite = Color(0xFFFFFFFF)
val MutedWhite = Color(0xFFAAAAAA)

private val MonochromeColorScheme = darkColorScheme(
    primary = CrispWhite,
    onPrimary = PitchBlack,
    secondary = CrispWhite,
    onSecondary = PitchBlack,
    background = PitchBlack,
    onBackground = CrispWhite,
    surface = DeepSurface,
    onSurface = CrispWhite,
    surfaceVariant = CardGray,
    onSurfaceVariant = MutedWhite,
    outline = BorderGray
)

@Composable
fun CardVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MonochromeColorScheme,
        content = content
    )
}
""",

    "app/src/main/java/com/secure/cardvault/MainActivity.kt": """package com.secure.cardvault

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.secure.cardvault.data.CardEntity
import com.secure.cardvault.data.VaultDatabase
import com.secure.cardvault.ui.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var isUnlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // REQUIREMENT 2: STRICT SCREEN PROTECTION (Block Screenshots & Recording)
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
                            onSaveCard = { card ->
                                lifecycleScope.launch { dao.insertCard(card) }
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
        // REQUIREMENT 3: Biometric Prompt on App Launch & Resume from Background
        if (!isUnlocked) {
            promptBiometric()
        }
    }

    override fun onPause() {
        super.onPause()
        // Re-lock immediately when leaving app
        isUnlocked = false
    }

    private fun promptBiometric() {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        if (biometricManager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            // Fallback if device has no pin/biometric enrolled during testing
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
fun LockedScreen(onUnlockRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Locked",
            tint = CrispWhite,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "VAULT LOCKED",
            color = CrispWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "100% Offline • SQLCipher AES-256",
            color = MutedWhite,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onUnlockRequest,
            border = BorderStroke(1.dp, CrispWhite),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrispWhite),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("UNLOCK VAULT", fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultAppScreen(
    cards: List<CardEntity>,
    onSaveCard: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

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
                        Text(
                            "CARDVAULT",
                            color = CrispWhite,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitchBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CrispWhite,
                contentColor = PitchBlack,
                shape = RoundedCornerShape(12.dp)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    MonochromeCardItem(
                        card = card,
                        onCopy = { label, value ->
                            copyToClipboard(context, label, value)
                            scope.launch {
                                snackbarHostState.showSnackbar("$label copied to clipboard")
                            }
                        },
                        onDelete = { onDeleteCard(card) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddCardDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newCard ->
                    onSaveCard(newCard)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun MonochromeCardItem(
    card: CardEntity,
    onCopy: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    val formattedNumber = remember(card.cardNumber, revealed) {
        val clean = card.cardNumber.replace(" ", "")
        if (revealed) {
            clean.chunked(4).joinToString(" ")
        } else {
            val last4 = if (clean.length >= 4) clean.takeLast(4) else clean
            "**** **** **** $last4"
        }
    }

    val isInverted = card.themeContrast == "Crisp White"
    val bgColor = if (isInverted) CrispWhite else CardGray
    val contentColor = if (isInverted) PitchBlack else CrispWhite
    val subColor = if (isInverted) Color(0xFF444444) else MutedWhite
    val borderColor = if (isInverted) CrispWhite else Color(0xFF3A3A3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row: Bank Name + Brand/Type + Eye Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = card.bankName.uppercase(),
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${card.brand} • ${card.cardType}",
                        color = subColor,
                        fontSize = 12.sp
                    )
                }
                Row {
                    IconButton(onClick = { revealed = !revealed }) {
                        Icon(
                            imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Reveal",
                            tint = contentColor
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Card",
                            tint = subColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Formatted 16-digit Number
            Text(
                text = formattedNumber,
                color = contentColor,
                fontSize = 21.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Expiry, CVV, Billing Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("CARDHOLDER", color = subColor, fontSize = 10.sp)
                    Text(card.cardholderName.uppercase(), color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Column {
                    Text("EXPIRES", color = subColor, fontSize = 10.sp)
                    Text(card.expiryDate, color = contentColor, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                }
                Column {
                    Text("CVV", color = subColor, fontSize = 10.sp)
                    Text(if (revealed) card.cvv else "***", color = contentColor, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                }
                Column {
                    Text("DUE DAY", color = subColor, fontSize = 10.sp)
                    Text("Day ${card.billingDueDay}", color = contentColor, fontSize = 13.sp)
                }
            }

            if (card.iban.isNotBlank() || card.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = borderColor, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                if (card.iban.isNotBlank()) {
                    Text(
                        text = "IBAN: ${if (revealed) card.iban else "****" + card.iban.takeLast(4)}",
                        color = subColor,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (card.notes.isNotBlank()) {
                    Text(
                        text = "Note: ${if (revealed) card.notes else "••••••••"}",
                        color = subColor,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // One-tap Copy Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onCopy("Card Number", card.cardNumber) },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, contentColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("COPY NUMBER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (card.iban.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onCopy("IBAN", card.iban) },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, contentColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY IBAN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onSave: (CardEntity) -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var cardholderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Credit") }
    var brand by remember { mutableStateOf("Visa") }
    var themeContrast by remember { mutableStateOf("Pitch Black") }
    var billingDueDay by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSurface,
        titleContentColor = CrispWhite,
        title = { Text("ADD NEW CARD", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
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
                    label = { Text("Bank Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() } },
                    label = { Text("16-Digit Card Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { if (it.length <= 5) expiryDate = it },
                        label = { Text("Expiry (MM/YY)") },
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
                }
                OutlinedTextField(
                    value = iban,
                    onValueChange = { iban = it.uppercase() },
                    label = { Text("IBAN (Optional)") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand (Visa/MC)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cardType,
                        onValueChange = { cardType = it },
                        label = { Text("Type (Credit/Debit)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = billingDueDay,
                        onValueChange = { billingDueDay = it.filter { c -> c.isDigit() } },
                        label = { Text("Due Day (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = themeContrast,
                        onValueChange = { themeContrast = it },
                        label = { Text("Style (Pitch Black / Crisp White)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
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
                                bankName = bankName.trim(),
                                cardholderName = cardholderName.trim().ifEmpty { "CARDHOLDER" },
                                cardNumber = cardNumber,
                                iban = iban.trim(),
                                expiryDate = expiryDate.ifEmpty { "12/29" },
                                cvv = cvv.ifEmpty { "000" },
                                cardType = cardType.ifEmpty { "Credit" },
                                brand = brand.ifEmpty { "Visa" },
                                themeContrast = themeContrast,
                                billingDueDay = billingDueDay.toIntOrNull()?.coerceIn(1, 31) ?: 1,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrispWhite, contentColor = PitchBlack)
            ) {
                Text("SAVE CARD", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = MutedWhite)
            }
        }
    )
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
"""
}

for path, content in files.items():
    dir_name = os.path.dirname(path)
    if dir_name:
        os.makedirs(dir_name, exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("CardVault Offline Native Android Project Generated Successfully!")
