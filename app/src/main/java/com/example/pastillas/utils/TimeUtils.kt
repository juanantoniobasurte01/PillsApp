package com.example.pastillas.utils

import java.util.Locale


// Registrar horarios para tomas (hay que probar esto)
object TimeUtils {

    fun obtenerHora(horario: String): Pair<Int, Int> {
        val normalizado = horario.trim().lowercase(Locale("es", "ES"))
        return when (normalizado) {
            "mañana", "manana" -> Pair(8, 0)
            "mediodía", "mediodia" -> Pair(12, 0)
            "almuerzo" -> Pair(14, 0)
            "tarde" -> Pair(17, 0)
            "cena" -> Pair(21, 0)
            "noche" -> Pair(23, 0)
            else -> Pair(8, 0)
        }
    }
}
