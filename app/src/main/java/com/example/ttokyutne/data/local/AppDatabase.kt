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
    version = 2,
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

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ttokyutne.db"
                )
                    .addMigrations(migration1To2)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
