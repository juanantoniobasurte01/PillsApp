package com.example.pastillas.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS historial_tomas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombreToma TEXT NOT NULL,
                    numeroPastillasEsperadas INTEGER NOT NULL,
                    pastillasDetectadas INTEGER NOT NULL,
                    correcta INTEGER NOT NULL,
                    fechaRegistro INTEGER NOT NULL,
                    horario TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pastillas_db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
