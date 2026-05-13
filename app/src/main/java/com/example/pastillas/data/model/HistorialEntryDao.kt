package com.example.pastillas.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistorialEntryDao {

    @Insert
    suspend fun insertarEntrada(entrada: HistorialEntry): Long

    @Query("SELECT * FROM historial_tomas ORDER BY fechaRegistro DESC")
    suspend fun obtenerHistorial(): List<HistorialEntry>
}
