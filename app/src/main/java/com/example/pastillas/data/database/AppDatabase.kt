package com.example.pastillas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pastillas.data.model.HistorialEntry
import com.example.pastillas.data.model.HistorialEntryDao
import com.example.pastillas.data.model.Toma
import com.example.pastillas.data.model.TomaDao

@Database(entities = [Toma::class, HistorialEntry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tomaDao(): TomaDao

    abstract fun historialEntryDao(): HistorialEntryDao
}
