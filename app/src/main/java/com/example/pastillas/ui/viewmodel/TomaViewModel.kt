package com.example.pastillas.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pastillas.data.database.DatabaseProvider
import com.example.pastillas.data.model.HistorialEntry
import com.example.pastillas.data.model.Toma
import com.example.pastillas.data.repository.HistorialRepository
import com.example.pastillas.data.repository.TomaRepository
import com.example.pastillas.ui.notificaciones.AlarmScheduler
import kotlinx.coroutines.launch

class TomaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TomaRepository
    private val historialRepository: HistorialRepository
    private val appContext = application.applicationContext
    val tomasDisponibles = mutableStateListOf<Toma>()
    val historial = mutableStateListOf<HistorialEntry>()

    init {
        val database = DatabaseProvider.getDatabase(application)
        repository = TomaRepository(database.tomaDao())
        historialRepository = HistorialRepository(database.historialEntryDao())

        // Cargar todas las tomas al iniciar
        viewModelScope.launch {
            val todas = repository.obtenerTodasLasTomas()
            tomasDisponibles.addAll(todas)

            val historialGuardado = historialRepository.obtenerHistorial()
            historial.addAll(historialGuardado)
        }
    }

    // Añadir una nueva toma
    fun agregarToma(context: Context, toma: Toma) = viewModelScope.launch {
        Log.d("ALARM", "ENTRA VIEWMODEL agregarToma")
        val insertedId = repository.agregarToma(toma).toInt()
        val tomaGuardada = toma.copy(id = insertedId)
        tomasDisponibles.add(tomaGuardada)

        AlarmScheduler.programarToma(context, tomaGuardada)
    }

    fun actualizarToma(context: Context, toma: Toma) = viewModelScope.launch {
        repository.actualizarToma(toma)
        val index = tomasDisponibles.indexOfFirst { it.id == toma.id }
        if (index >= 0) {
            tomasDisponibles[index] = toma
        }

        AlarmScheduler.cancelarToma(context, toma.id)
        if (toma.notificacionActiva) {
            AlarmScheduler.programarToma(context, toma)
        }
    }

    fun reprogramarTomas(context: Context) = viewModelScope.launch {
        tomasDisponibles.forEach { toma ->
            AlarmScheduler.cancelarToma(context, toma.id)
            if (toma.notificacionActiva) {
                AlarmScheduler.programarToma(context, toma)
            }
        }
    }

    // Eliminar una toma
    fun eliminarToma(toma: Toma) = viewModelScope.launch {
        AlarmScheduler.cancelarToma(appContext, toma.id)
        repository.borrarToma(toma)
        tomasDisponibles.remove(toma)
    }

    fun guardarEnHistorial(toma: Toma, pastillasDetectadas: Int, correcta: Boolean) = viewModelScope.launch {
        val entrada = HistorialEntry(
            nombreToma = toma.nombre,
            numeroPastillasEsperadas = toma.numeroPastillas,
            pastillasDetectadas = pastillasDetectadas,
            correcta = correcta,
            fechaRegistro = System.currentTimeMillis(),
            horario = toma.horario
        )
        val insertedId = historialRepository.guardarEntrada(entrada).toInt()
        historial.add(0, entrada.copy(id = insertedId))
    }
}
