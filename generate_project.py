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
        versionCode = 3
        versionName = "2.0.0"

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
    <!-- Outer Monochrome Shield -->
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3.5"
        android:fillColor="#0A0A0A"
        android:pathData="M54,24 L78,34 C78,57 54,82 54,82 C54,82 30,57 30,34 Z"/>
    <!-- Lock Shackle -->
    <path
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3.2"
        android:strokeLineCap="round"
        android:fillColor="#00000000"
        android:pathData="M47,49 V43 C47,39.1 50.1,36 54,36 C57.9,36 61,39.1 61,43 V49"/>
    <!-- Lock Body -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M43,49 h22 v16 h-22 z"/>
    <!-- Keyhole Cutout -->
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
    val cardType: String,     // Credit, Debit, Prepaid
    val brand: String,        // Visa, Mastercard, Amex, Meeza
    val themeContrast: String,// Pitch Black, Crisp White, Carbon Metal
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    RealisticBankCardItem(
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

// ==================== REALISTIC EMV CHIP & NFC WAVE GRAPHICS ====================

@Composable
fun RealisticEmvChip(isInverted: Boolean) {
    val chipBrush = if (isInverted) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFD6D6D6), Color(0xFF8C8C8C), Color(0xFFE8E8E8))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFF5D77F), Color(0xFFB8862D), Color(0xFFFCE8A6))
        )
    }
    val lineColor = if (isInverted) Color(0xFF444444) else Color(0xFF6E4E11)

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 35.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(chipBrush)
            .border(1.dp, lineColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Horizontal circuit lines
            drawLine(lineColor, Offset(0f, h * 0.33f), Offset(w, h * 0.33f), strokeWidth = 1.5f)
            drawLine(lineColor, Offset(0f, h * 0.66f), Offset(w, h * 0.66f), strokeWidth = 1.5f)
            // Vertical circuit lines
            drawLine(lineColor, Offset(w * 0.32f, 0f), Offset(w * 0.32f, h), strokeWidth = 1.5f)
            drawLine(lineColor, Offset(w * 0.68f, 0f), Offset(w * 0.68f, h), strokeWidth = 1.5f)
            // Center Contact Pad
            drawRoundRect(
                color = lineColor,
                topLeft = Offset(w * 0.32f, h * 0.25f),
                size = Size(w * 0.36f, h * 0.5f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 1.8f)
            )
        }
    }
}

@Composable
fun ContactlessNfcIcon(tint: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = 2.4f
        for (i in 1..4) {
            val radius = (size.minDimension / 5.5f) * i
            drawArc(
                color = tint.copy(alpha = 0.85f),
                startAngle = -42f,
                sweepAngle = 84f,
                useCenter = false,
                topLeft = Offset(size.width * 0.1f - radius / 2, size.height / 2 - radius / 2),
                size = Size(radius, radius),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun CardBrandLogo(brand: String, contentColor: Color) {
    val cleanBrand = brand.trim().lowercase()
    when {
        cleanBrand.contains("master") -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(38.dp).height(24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .background(contentColor.copy(alpha = 0.85f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterEnd)
                            .border(1.5.dp, contentColor, CircleShape)
                            .background(contentColor.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }
        }
        cleanBrand.contains("visa") -> {
            Text(
                text = "VISA",
                color = contentColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp
            )
        }
        else -> {
            Text(
                text = brand.uppercase(),
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ==================== REALISTIC PHYSICAL BANK CARD COMPONENT ====================

@Composable
fun RealisticBankCardItem(
    card: CardEntity,
    onCopy: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    val formattedNumber = remember(card.cardNumber, revealed) {
        val clean = card.cardNumber.replace(" ", "").padEnd(16, '•')
        if (revealed) {
            card.cardNumber.replace(" ", "").chunked(4).joinToString("   ")
        } else {
            val last4 = if (card.cardNumber.length >= 4) card.cardNumber.takeLast(4) else card.cardNumber
            "••••   ••••   ••••   $last4"
        }
    }

    val isInverted = card.themeContrast.equals("Crisp White", ignoreCase = true)

    val cardBrush = if (isInverted) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFEAEAEA),
                Color(0xFFDCDCDC)
            ),
            start = Offset(0f, 0f),
            end = Offset(900f, 600f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF222222),
                Color(0xFF0D0D0D),
                Color(0xFF050505),
                Color(0xFF1B1B1B)
            ),
            start = Offset(0f, 0f),
            end = Offset(1000f, 700f)
        )
    }

    val contentColor = if (isInverted) PitchBlack else CrispWhite
    val subColor = if (isInverted) Color(0xFF555555) else Color(0xFFA0A0A0)
    val borderColor = if (isInverted) Color(0xFFFFFFFF) else Color(0xFF3D3D3D)

    val embossedStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        shadow = if (!isInverted) {
            Shadow(color = Color.Black, offset = Offset(2f, 3f), blurRadius = 2f)
        } else {
            Shadow(color = Color(0x55000000), offset = Offset(1f, 2f), blurRadius = 1f)
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. REALISTIC ISO/IEC 7810 ID-1 CREDIT CARD SURFACE (Aspect Ratio 1.586)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .clip(RoundedCornerShape(18.dp))
                .background(cardBrush)
                .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
                .clickable { revealed = !revealed }
        ) {
            // Subtle Metallic Diagonal Glare Reflection
            Canvas(modifier = Modifier.fillMaxSize()) {
                val glareColor = if (isInverted) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.04f)
                drawCircle(
                    color = glareColor,
                    radius = size.maxDimension * 0.55f,
                    center = Offset(size.width * 0.88f, -size.height * 0.15f)
                )
                drawCircle(
                    color = glareColor,
                    radius = size.maxDimension * 0.4f,
                    center = Offset(size.width * 0.1f, size.height * 1.15f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP ROW: Bank Name + Card Type Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = card.bankName.uppercase(),
                            color = contentColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            letterSpacing = 1.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = contentColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.5.dp, contentColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = card.cardType.uppercase(),
                            color = contentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // MIDDLE ROW: Realistic EMV Chip + Contactless NFC Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RealisticEmvChip(isInverted = isInverted)
                        Spacer(modifier = Modifier.width(12.dp))
                        ContactlessNfcIcon(tint = contentColor)
                    }

                    // Due Day Pill
                    Text(
                        text = "BILLING DAY: ${card.billingDueDay}",
                        color = subColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }

                // EMBOSSED 16-DIGIT CARD NUMBER
                Text(
                    text = formattedNumber,
                    color = contentColor,
                    fontSize = 20.sp,
                    letterSpacing = 1.8.sp,
                    style = embossedStyle,
                    maxLines = 1
                )

                // BOTTOM ROW: Cardholder Name + Valid Thru + CVV + Brand Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Column {
                                Text(
                                    text = "VALID THRU",
                                    color = subColor,
                                    fontSize = 7.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = card.expiryDate,
                                    color = contentColor,
                                    fontSize = 13.sp,
                                    style = embossedStyle
                                )
                            }
                            Column {
                                Text(
                                    text = "SECURITY CVV",
                                    color = subColor,
                                    fontSize = 7.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (revealed) card.cvv else "•••",
                                    color = contentColor,
                                    fontSize = 13.sp,
                                    style = embossedStyle
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = card.cardholderName.uppercase(),
                            color = contentColor,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    CardBrandLogo(brand = card.brand, contentColor = contentColor)
                }
            }
        }

        // 2. SLEEK ACTION DOCK UNDER THE PHYSICAL CARD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .offset(y = (-4).dp),
            color = Color(0xFF0E0E0E),
            shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp),
            border = BorderStroke(0.8.dp, Color(0xFF282828))
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (card.iban.isNotBlank() || card.notes.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (card.iban.isNotBlank()) {
                            Text(
                                text = "IBAN: ${if (revealed) card.iban else "•••• " + card.iban.takeLast(4)}",
                                color = MutedWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (card.notes.isNotBlank()) {
                            Text(
                                text = if (revealed) card.notes else "Note: ••••••",
                                color = MutedWhite,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Divider(color = Color(0xFF222222), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reveal / Mask Toggle
                    TextButton(
                        onClick = { revealed = !revealed },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (revealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Mask",
                            tint = CrispWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (revealed) "HIDE" else "REVEAL",
                            color = CrispWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Copy Card Number
                    TextButton(
                        onClick = { onCopy("Card Number", card.cardNumber) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Card Number",
                            tint = CrispWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY CARD", color = CrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Copy IBAN (if available)
                    if (card.iban.isNotBlank()) {
                        TextButton(
                            onClick = { onCopy("IBAN", card.iban) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Copy IBAN",
                                tint = CrispWhite,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("IBAN", color = CrispWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Card",
                            tint = MutedWhite,
                            modifier = Modifier.size(18.dp)
                        )
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
        title = { Text("ADD REALISTIC CARD", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
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
                        label = { Text("Brand (Visa / Mastercard)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cardType,
                        onValueChange = { cardType = it },
                        label = { Text("Type (Credit / Debit)") },
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
                        label = { Text("Finish (Pitch Black / Crisp White)") },
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

print("CardVault Offline Realistic 3D Bank Card Edition Generated Successfully!")
