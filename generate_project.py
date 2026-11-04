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
        versionCode = 6
        versionName = "4.1.0"

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

    "app/src/main/res/drawable/ic_launcher_background.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#000000"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
""",

    "app/src/main/res/drawable/ic_launcher_foreground.xml": """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3.5"
        android:fillColor="#0A0A0A"
        android:pathData="M54,24 L78,34 C78,57 54,82 54,82 C54,82 30,57 30,34 Z"/>
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3.2"
        android:strokeLineCap="round"
        android:fillColor="#00000000"
        android:pathData="M47,49 V43 C47,39.1 50.1,36 54,36 C57.9,36 61,39.1 61,43 V49"/>
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M43,49 h22 v16 h-22 z"/>
    <path
        android:fillColor="#000000"
        android:pathData="M52.5,54 h3 v6 h-3 z"/>
</vector>
""",

    "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml": """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
""",

    "app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml": """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
""",

    "app/src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- STRICT OFFLINE MODE: NO INTERNET PERMISSION INCLUDED -->
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />

    <application
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
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
    val atmPin: String,
    val cardType: String,
    val brand: String,
    val themeContrast: String,
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
    suspend fun insertOrUpdateCard(card: CardEntity)

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

@Database(entities = [CardEntity::class], version = 2, exportSchema = false)
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

val PitchBlack = Color(0xFF000000)
val DeepSurface = Color(0xFF0A0A0A)
val CardGray = Color(0xFF141414)
val BorderGray = Color(0xFF333333)
val CrispWhite = Color(0xFFFFFFFF)
val MutedWhite = Color(0xFF9E9E9E)

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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
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
import com.secure.cardvault.ui.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var isUnlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STRICT SCREEN PROTECTION (Block Screenshots & Recording)
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
    onSaveOrUpdateCard: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                                "TAP CARD TO FLIP 3D • FRONT / BACK",
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(26.dp),
                contentPadding = PaddingValues(vertical = 18.dp)
            ) {
                items(cards, key = { it.id }) { card ->
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

// ==================== PRECISION EMV CHIP, HOLOGRAM & BRAND LOGOS ====================

@Composable
fun LuxuryEmvChip(isInverted: Boolean) {
    val chipBrush = if (isInverted) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFCCCCCC), Color(0xFF777777), Color(0xFFF2F2F2), Color(0xFF999999))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFFE596), Color(0xFFB38228), Color(0xFFFFF3C4), Color(0xFF8F6314))
        )
    }
    val lineColor = if (isInverted) Color(0xFF333333) else Color(0xFF593D08)

    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 38.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(chipBrush)
            .border(1.dp, lineColor.copy(alpha = 0.8f), RoundedCornerShape(7.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
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
}

@Composable
fun ContactlessNfcWaves(tint: Color) {
    Canvas(modifier = Modifier.size(26.dp)) {
        val stroke = 2.5f
        for (i in 1..4) {
            val radius = (size.minDimension / 5.2f) * i
            drawArc(
                color = tint.copy(alpha = 0.85f),
                startAngle = -40f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = Offset(size.width * 0.1f - radius / 2, size.height / 2 - radius / 2),
                size = Size(radius, radius),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun SecurityHologramSticker() {
    val holoBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF888888),
            Color(0xFFE8E8E8),
            Color(0xFF707070),
            Color(0xFFFFFFFF),
            Color(0xFF999999)
        )
    )
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(holoBrush)
            .border(0.8.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Black.copy(alpha = 0.25f),
                radius = size.minDimension * 0.35f,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.20f),
                radius = size.minDimension * 0.20f,
                style = Stroke(width = 1.2f)
            )
        }
        Text(
            text = "SECURE",
            color = Color.Black.copy(alpha = 0.65f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun LuxuryBrandMark(brand: String, contentColor: Color) {
    val cleanBrand = brand.trim().lowercase()
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

// ==================== ULTRA-LUXURY 3D FLIPPING BANK CARD ====================

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
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        label = "Card3DFlip"
    )

    val isInverted = card.themeContrast.equals("Crisp White", ignoreCase = true)
    val isLuhnValid = remember(card.cardNumber) { isValidLuhnNumber(card.cardNumber) }

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

    val frontBrush = if (isInverted) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF0F0F0),
                Color(0xFFDCDCDC),
                Color(0xFFF8F8F8)
            ),
            start = Offset(0f, 0f),
            end = Offset(1000f, 700f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF282828),
                Color(0xFF101010),
                Color(0xFF050505),
                Color(0xFF1E1E1E)
            ),
            start = Offset(0f, 0f),
            end = Offset(1000f, 700f)
        )
    }

    val contentColor = if (isInverted) PitchBlack else CrispWhite
    val subColor = if (isInverted) Color(0xFF555555) else Color(0xFFA5A5A5)
    val rimColor = if (isInverted) Color(0xFFBBBBBB) else Color(0xFF424242)

    val embossedNumberStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 21.sp,
        letterSpacing = 2.sp,
        shadow = if (!isInverted) {
            Shadow(color = Color.Black, offset = Offset(3f, 4f), blurRadius = 3f)
        } else {
            Shadow(color = Color(0x55000000), offset = Offset(1f, 2f), blurRadius = 2f)
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // 3D FLIPPING CONTAINER (ISO/IEC 7810 ID-1 1.586 Aspect Ratio)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 16f * density
                }
                .shadow(14.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(frontBrush)
                .border(1.4.dp, rimColor, RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isFlippedToBack = !isFlippedToBack
                }
        ) {
            // Precision Laser-Engraved Guilloche Circles & Metallic Sheen Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val patternColor = if (isInverted) Color.Black.copy(alpha = 0.035f) else Color.White.copy(alpha = 0.035f)
                for (r in 1..9) {
                    drawCircle(
                        color = patternColor,
                        radius = (size.minDimension * 0.14f) * r,
                        center = Offset(size.width * 0.85f, size.height * 0.50f),
                        style = Stroke(width = 1.2f)
                    )
                }
                val sheenPath = Path().apply {
                    moveTo(0f, size.height * 0.35f)
                    lineTo(size.width * 0.65f, 0f)
                    lineTo(size.width * 0.90f, 0f)
                    lineTo(0f, size.height * 0.65f)
                    close()
                }
                drawPath(
                    path = sheenPath,
                    color = if (isInverted) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.03f)
                )
            }

            // Inner metallic bezel frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .border(0.5.dp, contentColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            )

            if (rotationY <= 90f) {
                // ==================== FRONT OF THE PHYSICAL BANK CARD ====================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // TOP ROW: Bank Name + Luxury Tier Badge
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
                                text = "${card.cardType.uppercase()} • WORLD ELITE",
                                color = contentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // SECOND ROW: Precision Gold/Silver EMV Chip + Contactless NFC Waves
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LuxuryEmvChip(isInverted = isInverted)
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

                    // THIRD ROW: 4 evenly spaced embossed digit groups + micro-BIN
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
                                    style = embossedNumberStyle
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

                    // BOTTOM ROW: Cardholder Name + Valid Thru + Brand Logo
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
                                        Text(
                                            text = "VALID",
                                            color = subColor,
                                            fontSize = 6.sp,
                                            letterSpacing = 0.8.sp
                                        )
                                        Text(
                                            text = "THRU",
                                            color = subColor,
                                            fontSize = 6.sp,
                                            letterSpacing = 0.8.sp
                                        )
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
                // ==================== BACK OF THE PHYSICAL BANK CARD (Rotated 180°) ====================
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
                        // Realistic Pitch-Black Magnetic Stripe
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .background(Color(0xFF030303))
                                .border(0.5.dp, Color(0xFF2B2B2B))
                        )
                    }

                    // Signature Strip + CVV Security Box + Hologram Sticker
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

                    // Encrypted ATM PIN, IBAN & Notes Bunker on the Back
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        color = contentColor.copy(alpha = 0.07f),
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

                    // Footer instruction
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

        // 2. SLEEK TACTICAL ACTION DOCK UNDER THE 3D CARD
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
                // 3D Flip Button
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

                // Reveal / Mask Toggle
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

                // Copy Card Number
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

                // Copy IBAN or PIN
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

                // Edit & Delete Icons
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

// ==================== ADD & EDIT CARD DIALOG WITH TOUCH CHIPS & RADAR ====================

@OptIn(ExperimentalMaterial3Api::class)
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

                // 16-Digit Card Number with Live Auto-Brand & Luhn Checksum Radar
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

                // Expiry, CVV & Encrypted ATM PIN Row
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

                // Touch Selector for Card Type (Credit / Debit / Prepaid)
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

                // Touch Selector for Card Finish (Pitch Black / Crisp White)
                Text("Card Metal Finish:", color = MutedWhite, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pitch Black", "Crisp White").forEach { styleOption ->
                        FilterChip(
                            selected = themeContrast.equals(styleOption, ignoreCase = true),
                            onClick = { themeContrast = styleOption },
                            label = { Text(styleOption, fontSize = 11.sp) }
                        )
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

print("CardVault Offline v4.1 (Fixed Build & Touch Chips Edition) Generated Successfully!")
