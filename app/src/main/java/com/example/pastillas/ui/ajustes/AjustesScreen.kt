package com.example.pastillas.ui.ajustes

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastillas.ui.components.botones.PillSwitch


@Composable
fun AjustesScreen(navController: NavController, isDarkMode: MutableState<Boolean>) {
    var terceraEdad by remember { mutableStateOf(false) }
    var notificacionActiva by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Título de la pantalla
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Modo Claro/Oscuro
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Modo Oscuro",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            PillSwitch(
                checked = isDarkMode.value,
                onCheckedChange = { isDarkMode.value = it },
            )
        }

        // Modo Tercera Edad (ahora mismo no hace nada)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Modo Tercera Edad",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            PillSwitch(
                checked = terceraEdad,
                onCheckedChange = { terceraEdad = it }
            )
        }

        // Notificación / Alarma (todavia no hace nada)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Modo Notificación",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            PillSwitch(
                checked = notificacionActiva,
                onCheckedChange = { notificacionActiva = it }
            )
        }

    }
}