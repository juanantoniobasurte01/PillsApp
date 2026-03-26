package com.example.pastillas.data.repository

import com.example.pastillas.data.model.TomaDao
import com.example.pastillas.data.model.Toma

class TomaRepository(private val dao: TomaDao) {

    // Insertar una nueva toma
    suspend fun agregarToma(toma: Toma) = dao.insertarToma(toma)

    // Borrar una toma
    suspend fun borrarToma(toma: Toma) = dao.borrarToma(toma)

    // Obtener todas las tomas ordenadas por fecha
    suspend fun obtenerTodasLasTomas() = dao.obtenerTodasLasTomas()

    // Obtener tomas de un día específico
    suspend fun obtenerTomasPorDia(inicioDia: Long, finDia: Long) =
        dao.obtenerTomasPorDia(inicioDia, finDia)

    // Obtener tomas por horario (mañana, almuerzo, tarde...)
    suspend fun obtenerTomasPorHorario(horario: String) =
        dao.obtenerTomasPorHorario(horario)

    // Contar tomas correctas
    suspend fun contarTomasCorrectas() = dao.contarTomasCorrectas()

    // Contar tomas incorrectas
    suspend fun contarTomasIncorrectas() = dao.contarTomasIncorrectas()
}