package com.example.pastillas.ui.tomas

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    viewModel: TomaViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var numPastillas by remember { mutableStateOf(1) }
    var horario by remember { mutableStateOf("Mañana") }
    var notificacion by remember { mutableStateOf(true) }
    var expandedCantidad by remember { mutableStateOf(false) }
    val cantidades = (1..6).toList()


    var expandedHorario by remember { mutableStateOf(false) }
    val horarios = listOf("Mañana", "Mediodía", "Almuerzo", "Tarde", "Cena", "Noche")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // NOMBRE
        CajaIntroducirNombre(
            value = nombre,
            onValueChange = { if (it.length <= 30) nombre = it },
            placeholder = "Nombre de la toma",
            modifier = Modifier.fillMaxWidth()
        )

        //DESCRIPCIÓN
        CajaDescripcion(
            value = descripcion,
            onValueChange = { descripcion = it }
        )



        // CANTIDAD DE PASTILLAS
        CajaCantidadPastillas(
            numPastillas = numPastillas,
            cantidades = cantidades,
            onCantidadSelected = { cantidad -> numPastillas = cantidad },
            modifier = Modifier.fillMaxWidth()
        )

        // HORARIO
        CajaHorario(
            value = horario,
            horarios = horarios,
            expandedHorario = expandedHorario,
            onExpandedChange = { expandedHorario = it },
            onHorarioSelected = { horario = it },
            modifier = Modifier.fillMaxWidth()
        )

        //NOTIFICACIÓN
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PillSwitch(checked = notificacion, onCheckedChange = { notificacion = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Habilitar notificación")
        }

        Spacer(modifier = Modifier.height(24.dp))

        //GUARDAR BUTT
        BotonGuardar(
            text = "GUARDAR",
            onClick = {
                //  Si el nombre está vacio, ponemos el texto por defecto
                val nombreFinal = if (nombre.isBlank()) "Toma sin  nombre" else nombre


                val nuevaToma = Toma(
                    nombre = nombreFinal,
                    descripcion = descripcion,
                    numeroPastillas = numPastillas,
                    pastillasDetectadas = null,
                    diaYfecha = System.currentTimeMillis(),
                    horario = horario,
                    correcta = true,
                    notificacionActiva = notificacion
                )

                viewModel.agregarToma(nuevaToma)
                navController.navigate("tomas_disponibles") {
                    popUpTo("tomas_disponibles") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
