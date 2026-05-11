package com.example.pastillas.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Archivo para guardar la configuracion de ajustes.
val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
    val MODO_TERCERA_EDAD = booleanPreferencesKey("modo_tercera_edad")
    val MODO_PRUEBAS = booleanPreferencesKey("modo_pruebas")
    val HORA_PRUEBAS = intPreferencesKey("hora_pruebas")
    val MINUTO_PRUEBAS = intPreferencesKey("minuto_pruebas")
}

object SettingsDefaults {
    const val MODO_OSCURO = false
    const val MODO_TERCERA_EDAD = false
    const val MODO_PRUEBAS = false
    const val HORA_PRUEBAS = 23
    const val MINUTO_PRUEBAS = 0
}

class SettingsDataStore(private val context: Context) {

    private val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "No se pudieron leer los ajustes guardados.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    val modoOscuroFlow: Flow<Boolean> =
        preferencesFlow.map { preferences ->
            preferences[SettingsKeys.MODO_OSCURO] ?: SettingsDefaults.MODO_OSCURO
        }

    val modoTerceraEdadFlow: Flow<Boolean> =
        preferencesFlow.map { preferences ->
            preferences[SettingsKeys.MODO_TERCERA_EDAD] ?: SettingsDefaults.MODO_TERCERA_EDAD
        }

    val modoPruebasFlow: Flow<Boolean> =
        preferencesFlow.map { preferences ->
            preferences[SettingsKeys.MODO_PRUEBAS] ?: SettingsDefaults.MODO_PRUEBAS
        }

    val horaPruebasFlow: Flow<Int> =
        preferencesFlow.map { preferences ->
            preferences[SettingsKeys.HORA_PRUEBAS] ?: SettingsDefaults.HORA_PRUEBAS
        }

    val minutoPruebasFlow: Flow<Int> =
        preferencesFlow.map { preferences ->
            preferences[SettingsKeys.MINUTO_PRUEBAS] ?: SettingsDefaults.MINUTO_PRUEBAS
        }

    suspend fun guardarModoOscuro(valor: Boolean) {
        guardarBooleano(SettingsKeys.MODO_OSCURO, valor)
    }

    suspend fun guardarModoTerceraEdad(valor: Boolean) {
        guardarBooleano(SettingsKeys.MODO_TERCERA_EDAD, valor)
    }

    suspend fun guardarModoPruebas(valor: Boolean) {
        guardarBooleano(SettingsKeys.MODO_PRUEBAS, valor)
    }

    suspend fun guardarHoraPruebas(hora: Int) {
        guardarEntero(SettingsKeys.HORA_PRUEBAS, hora)
    }

    suspend fun guardarMinutoPruebas(minuto: Int) {
        guardarEntero(SettingsKeys.MINUTO_PRUEBAS, minuto)
    }

    private suspend fun guardarBooleano(key: Preferences.Key<Boolean>, valor: Boolean) {
        runCatching {
            context.dataStore.edit { preferences ->
                preferences[key] = valor
            }
        }.onFailure { exception ->
            Log.e(TAG, "No se pudo guardar un ajuste booleano.", exception)
        }
    }

    private suspend fun guardarEntero(key: Preferences.Key<Int>, valor: Int) {
        runCatching {
            context.dataStore.edit { preferences ->
                preferences[key] = valor
            }
        }.onFailure { exception ->
            Log.e(TAG, "No se pudo guardar un ajuste numerico.", exception)
        }
    }

    private companion object {
        const val TAG = "SettingsDataStore"
    }
}
