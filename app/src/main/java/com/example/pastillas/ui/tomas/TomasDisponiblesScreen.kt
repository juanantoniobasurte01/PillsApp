package com.example.pastillas.ui.tomas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastillas.data.model.Toma
import com.example.pastillas.ui.components.botones.BotonCerrarEliminar
import com.example.pastillas.ui.components.botones.BotonNuevaToma
import com.example.pastillas.ui.components.cards.CardTomaDisponible
import com.example.pastillas.ui.viewmodel.TomaViewModel

@Composable
fun TomasDisponiblesScreen(
    navController: NavController,
    viewModel: TomaViewModel = viewModel(),
    isDarkMode: Boolean
) {
    var selectedToma by remember { mutableStateOf<Toma?>(null) }

    val tomas = viewModel.tomasDisponibles

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            items(tomas) { toma ->
                CardTomaDisponible(
                    toma = toma,
                    isDark = isDarkMode,
                    onClick = { selectedToma = toma },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        BotonNuevaToma(
            text = "AÑADIR NUEVA TOMA",
            onClick = { navController.navigate("registro_toma") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

    if (selectedToma != null) {
        AlertDialog(
            onDismissRequest = { selectedToma = null },
            title = { Text(selectedToma!!.nombre) },
            text = {
                Column {
                    Text("Descripción: ${selectedToma!!.descripcion}")
                    Text("Horario aprox: ${selectedToma!!.horario}")
                    Text("Notificación: ${if (selectedToma!!.notificacionActiva) "Sí" else "No"}")
                    Spacer(modifier = Modifier.height(24.dp))
                    BotonCerrarEliminar(
                        text = "EDITAR",
                        onClick = {
                            navController.navigate("registro_toma/${selectedToma!!.id}")
                            selectedToma = null
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BotonCerrarEliminar(
                        text = "ELIMINAR",
                        onClick = {
                            viewModel.eliminarToma(selectedToma!!)
                            selectedToma = null
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                BotonCerrarEliminar(
                    text = "CERRAR",
                    onClick = { selectedToma = null }
                )
            }
        )
    }
}
