package com.example.ttokyutne.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ScreenOnEventEntity::class,
        UserSettingsEntity::class,
        PhraseHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenOnEventDao(): ScreenOnEventDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun phraseHistoryDao(): PhraseHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_settings (
                        id INTEGER NOT NULL PRIMARY KEY,
                        notificationEnabled INTEGER NOT NULL,
                        minIntervalSeconds INTEGER NOT NULL,
                        quietHoursEnabled INTEGER NOT NULL,
                        dataRetentionDays INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS phrase_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        phraseId INTEGER NOT NULL,
                        screenOnEventId INTEGER,
                        shownAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val migration2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE user_settings
                    ADD COLUMN vibrationEnabled INTEGER NOT NULL DEFAULT 1
                    """.trimIndent()
                )
            }
        }

        private val migration3To4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE user_settings
                    ADD COLUMN recheckAlertMode TEXT NOT NULL DEFAULT 'WITH_PHRASE'
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE user_settings
                    SET recheckAlertMode = CASE
                        WHEN notificationEnabled = 0 THEN 'SIMPLE'
                        ELSE 'WITH_PHRASE'
                    END
                    """.trimIndent()
                )
            }
        }

        private val migration4To5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE user_settings
                    ADD COLUMN onboardingCompleted INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE user_settings
                    SET onboardingCompleted = 1
                    """.trimIndent()
                )
            }
        }

        private val migration5To6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE user_settings
                    ADD COLUMN monitoringEnabled INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE user_settings
                    SET monitoringEnabled = 1
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ttokyutne.db"
                )
                    .addMigrations(
                        migration1To2,
                        migration2To3,
                        migration3To4,
                        migration4To5,
                        migration5To6
                    )
                    .build()
                    .also { instance = it }
            }
        }
    }
}
