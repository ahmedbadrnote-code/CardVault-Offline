package com.secure.cardvault.data

import android.content.Context
import android.util.Base64
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(entities = [CardEntity::class], version = 2, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        /**
         * Preserve existing cards when upgrading the original v1 schema.
         * The old billingDueDay field was removed in v2 and atmPin was added.
         * Existing cards receive an empty PIN value; users can fill it in later.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE cards_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        bankName TEXT NOT NULL,
                        cardholderName TEXT NOT NULL,
                        cardNumber TEXT NOT NULL,
                        iban TEXT NOT NULL,
                        expiryDate TEXT NOT NULL,
                        cvv TEXT NOT NULL,
                        atmPin TEXT NOT NULL,
                        cardType TEXT NOT NULL,
                        brand TEXT NOT NULL,
                        themeContrast TEXT NOT NULL,
                        notes TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO cards_new (
                        id, bankName, cardholderName, cardNumber, iban,
                        expiryDate, cvv, atmPin, cardType, brand, themeContrast, notes
                    )
                    SELECT
                        id, bankName, cardholderName, cardNumber, iban,
                        expiryDate, cvv, '', cardType, brand, themeContrast, notes
                    FROM cards
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE cards")
                database.execSQL("ALTER TABLE cards_new RENAME TO cards")
            }
        }

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
                    .addMigrations(MIGRATION_1_2)
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
