package com.example.pastillas.ui.tomas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastillas.data.model.Toma
import com.example.pastillas.ui.components.botones.BotonGuardar
import com.example.pastillas.ui.components.botones.PillSwitch
import com.example.pastillas.ui.components.texedits.CajaCantidadPastillas
import com.example.pastillas.ui.components.texedits.CajaDescripcion
import com.example.pastillas.ui.components.texedits.CajaHorario
import com.example.pastillas.ui.components.texedits.CajaIntroducirNombre
import com.example.pastillas.ui.viewmodel.TomaViewModel

@Composable
fun RegistroTomaScreen(
    navController: NavController,
    viewModel: TomaViewModel,
    tomaToEdit: Toma? = null
) {
    val isEditing = tomaToEdit != null
    var nombre by rememberSaveable(tomaToEdit?.id) {
        mutableStateOf(tomaToEdit?.nombre.orEmpty())
    }
    var descripcion by rememberSaveable(tomaToEdit?.id) {
        mutableStateOf(tomaToEdit?.descripcion.orEmpty())
    }
    var numPastillas by rememberSaveable(tomaToEdit?.id) {
        mutableStateOf(tomaToEdit?.numeroPastillas ?: 1)
    }
    var horario by rememberSaveable(tomaToEdit?.id) {
        mutableStateOf(tomaToEdit?.horario ?: "Mañana")
    }
    var notificacion by rememberSaveable(tomaToEdit?.id) {
        mutableStateOf(tomaToEdit?.notificacionActiva ?: true)
    }
    val cantidades = (1..6).toList()

    var expandedHorario by remember { mutableStateOf(false) }
    val horarios = listOf("Mañana", "Mediodía", "Almuerzo", "Tarde", "Cena", "Noche")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CajaIntroducirNombre(
            value = nombre,
            onValueChange = { if (it.length <= 30) nombre = it },
            placeholder = "Nombre de la toma",
            modifier = Modifier.fillMaxWidth()
        )

        CajaDescripcion(
            value = descripcion,
            onValueChange = { descripcion = it }
        )

        CajaCantidadPastillas(
            numPastillas = numPastillas,
            cantidades = cantidades,
            onCantidadSelected = { cantidad -> numPastillas = cantidad },
            modifier = Modifier.fillMaxWidth()
        )

        CajaHorario(
            value = horario,
            horarios = horarios,
            expandedHorario = expandedHorario,
            onExpandedChange = { expandedHorario = it },
            onHorarioSelected = { horario = it },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PillSwitch(checked = notificacion, onCheckedChange = { notificacion = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Habilitar notificacion",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BotonGuardar(
            text = if (isEditing) "GUARDAR CAMBIOS" else "GUARDAR",
            onClick = {
                val nombreFinal = if (nombre.isBlank()) "Toma sin  nombre" else nombre

                val tomaGuardada = Toma(
                    id = tomaToEdit?.id ?: 0,
                    nombre = nombreFinal,
                    descripcion = descripcion,
                    numeroPastillas = numPastillas,
                    pastillasDetectadas = tomaToEdit?.pastillasDetectadas,
                    diaYfecha = tomaToEdit?.diaYfecha ?: System.currentTimeMillis(),
                    horario = horario,
                    correcta = tomaToEdit?.correcta ?: true,
                    notificacionActiva = notificacion
                )

                if (isEditing) {
                    viewModel.actualizarToma(context, tomaGuardada)
                } else {
                    viewModel.agregarToma(context, tomaGuardada)
                }

                navController.navigate("tomas_disponibles") {
                    popUpTo("tomas_disponibles") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
