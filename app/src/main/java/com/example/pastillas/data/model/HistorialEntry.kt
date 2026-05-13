package com.example.pastillas.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_tomas")
data class HistorialEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreToma: String,
    val numeroPastillasEsperadas: Int,
    val pastillasDetectadas: Int,
    val correcta: Boolean,
    val fechaRegistro: Long,
    val horario: String
)
