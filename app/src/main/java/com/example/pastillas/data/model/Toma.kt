package com.example.pastillas.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tomas")
data class Toma(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val numeroPastillas: Int,
    val pastillasDetectadas: Int? = null,
    val diaYfecha: Long,
    val horario: String,
    val correcta: Boolean,
    val notificacionActiva: Boolean


)
