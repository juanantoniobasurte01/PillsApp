package com.example.pastillas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pastillas.data.model.Toma
import com.example.pastillas.data.model.TomaDao

@Database(entities = [Toma::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tomaDao(): TomaDao
}