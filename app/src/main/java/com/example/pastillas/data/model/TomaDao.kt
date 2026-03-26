package com.example.pastillas.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TomaDao {

    // Insertar una nueva toma
    @Insert
    suspend fun insertarToma(toma: Toma)

    // Borrar una toma
    @Delete
    suspend fun borrarToma(toma: Toma)

    // Consultar todas las tomas, ordenadas por fecha
    @Query("SELECT * FROM tomas ORDER BY diaYfecha ASC")
    suspend fun obtenerTodasLasTomas(): List<Toma>

    // Consultar tomas por día específico
    @Query("SELECT * FROM tomas WHERE diaYfecha BETWEEN :inicioDia AND :finDia ORDER BY diaYfecha ASC")
    suspend fun obtenerTomasPorDia(inicioDia: Long, finDia: Long): List<Toma>

    // Consultar tomas por horario
    @Query("SELECT * FROM tomas WHERE horario = :horario ORDER BY diaYfecha ASC")
    suspend fun obtenerTomasPorHorario(horario: String): List<Toma>

    // Contar tomas correctas e incorrectas
    @Query("SELECT COUNT(*) FROM tomas WHERE correcta = 1")
    suspend fun contarTomasCorrectas(): Int

    @Query("SELECT COUNT(*) FROM tomas WHERE correcta = 0")
    suspend fun contarTomasIncorrectas(): Int
}