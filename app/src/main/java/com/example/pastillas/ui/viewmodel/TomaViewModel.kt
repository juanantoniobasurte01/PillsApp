package com.example.pastillas.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pastillas.data.database.DatabaseProvider
import com.example.pastillas.data.model.Toma
import com.example.pastillas.data.repository.TomaRepository
import com.example.pastillas.ui.notificaciones.AlarmScheduler
import kotlinx.coroutines.launch

class TomaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TomaRepository
    val tomasDisponibles = mutableStateListOf<Toma>()

    init {
        val dao = DatabaseProvider.getDatabase(application).tomaDao()
        repository = TomaRepository(dao)

        // Cargar todas las tomas al iniciar
        viewModelScope.launch {
            val todas = repository.obtenerTodasLasTomas()
            tomasDisponibles.addAll(todas)
        }
    }

    // Añadir una nueva toma
    fun agregarToma(context: Context, toma: Toma) = viewModelScope.launch {
        Log.d("ALARM", "ENTRA VIEWMODEL agregarToma")
        repository.agregarToma(toma)
        tomasDisponibles.add(toma)

        AlarmScheduler.programarToma(context, toma)
    }



    // Eliminar una toma
    fun eliminarToma(toma: Toma) = viewModelScope.launch {
        repository.borrarToma(toma)
        tomasDisponibles.remove(toma)
    }
}