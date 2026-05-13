package com.example.pastillas.data.repository

import com.example.pastillas.data.model.HistorialEntry
import com.example.pastillas.data.model.HistorialEntryDao

class HistorialRepository(private val dao: HistorialEntryDao) {

    suspend fun guardarEntrada(entrada: HistorialEntry) = dao.insertarEntrada(entrada)

    suspend fun obtenerHistorial() = dao.obtenerHistorial()
}
