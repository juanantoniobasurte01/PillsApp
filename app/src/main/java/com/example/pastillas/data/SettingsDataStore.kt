package com.example.pastillas.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

    //Archivo para guardar la configuración de ajustes (falta implementar cosas y registrar ajustes)
val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val MODO_NOTIFICACION = booleanPreferencesKey("modo_notificacion")
    val MODO_PRUEBAS = booleanPreferencesKey("modo_pruebas")
    val HORA_PRUEBAS = intPreferencesKey("hora_pruebas")
    val MINUTO_PRUEBAS = intPreferencesKey("minuto_pruebas")
}

class SettingsDataStore(private val context: Context) {

    val modoNotificacionFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[SettingsKeys.MODO_NOTIFICACION] ?: true
        }

    val modoPruebasFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[SettingsKeys.MODO_PRUEBAS] ?: false
        }

    val horaPruebasFlow: Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[SettingsKeys.HORA_PRUEBAS] ?: 23
        }

    val minutoPruebasFlow: Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[SettingsKeys.MINUTO_PRUEBAS] ?: 0
        }

    suspend fun guardarModoNotificacion(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.MODO_NOTIFICACION] = valor
        }
    }

    suspend fun guardarModoPruebas(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.MODO_PRUEBAS] = valor
        }
    }

    suspend fun guardarHoraPruebas(hora: Int) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.HORA_PRUEBAS] = hora
        }
    }

    suspend fun guardarMinutoPruebas(minuto: Int) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.MINUTO_PRUEBAS] = minuto
        }
    }
}
