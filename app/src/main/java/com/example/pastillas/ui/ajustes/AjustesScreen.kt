package com.example.pastillas.ui.ajustes

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.app.NotificationManagerCompat
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.ui.components.botones.PillSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AjustesScreen(navController: NavController, isDarkMode: MutableState<Boolean>) {

    //Imports para guardar ajustes
    val context = LocalContext.current
    val settings = remember { SettingsDataStore(context) }



    var terceraEdad by remember { mutableStateOf(false) }
    val modoNotificacion by settings.modoNotificacionFlow.collectAsState(initial = true)
    val modoPruebas by settings.modoPruebasFlow.collectAsState(initial = false)
    val horaPruebas by settings.horaPruebasFlow.collectAsState(initial = 23)
    val minutoPruebas by settings.minutoPruebasFlow.collectAsState(initial = 0)

    var horaInput by remember(horaPruebas) { mutableStateOf(horaPruebas.toString()) }
    var minutoInput by remember(minutoPruebas) { mutableStateOf(minutoPruebas.toString()) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    val exactAlarmsAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager?.canScheduleExactAlarms() == true
    } else {
        true
    }



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
        // Qiero que los botones del inicio de abajo los ponga en grises, tambien aumentar mas el boton central y todos los botones y aumentar textos

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

        // Notificación / Alarma
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Modo Notificacion (${if (modoNotificacion) "Alarma" else "Notificacion"})",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            PillSwitch(
                checked = modoNotificacion,
                onCheckedChange = { value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        settings.guardarModoNotificacion(value)
                    }
                }
            )
        }



        // Boton permisis, si el usuario lo concede, el sistema permitira mostrar notificaciones.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsEnabled) {
            Button(
                onClick = {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Permitir notificaciones")
            }
        }


        // Boton que abre los ajustes de  alarmas para permitir full-screen
        if (!notificationsEnabled) {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            putExtra(Settings.EXTRA_CHANNEL_ID, "alarmas")
                        }
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajustes de notificacion (Alarmas)")
            }
        }



        // Boton: abre la pantalla del sistema para permitir alarmas exactas


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !exactAlarmsAllowed) {
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Permitir alarmas exactas")
            }
        }

        // Hora fija para pruebas (comentar/descomentar)

        /*
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Modo Pruebas (hora fija)",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )

            PillSwitch(
                checked = modoPruebas,
                onCheckedChange = { value ->
                    CoroutineScope(Dispatchers.IO).launch {
                        settings.guardarModoPruebas(value)
                    }
                }
            )
        }

        if (modoPruebas) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = horaInput,
                    onValueChange = { value ->
                        val filtered = value.filter { it.isDigit() }.take(2)
                        horaInput = filtered
                        val hora = filtered.toIntOrNull()
                        if (hora != null && hora in 0..23) {
                            CoroutineScope(Dispatchers.IO).launch {
                                settings.guardarHoraPruebas(hora)
                            }
                        }
                    },
                    label = { Text("Hora (0-23)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = minutoInput,
                    onValueChange = { value ->
                        val filtered = value.filter { it.isDigit() }.take(2)
                        minutoInput = filtered
                        val minuto = filtered.toIntOrNull()
                        if (minuto != null && minuto in 0..59) {
                            CoroutineScope(Dispatchers.IO).launch {
                                settings.guardarMinutoPruebas(minuto)
                            }
                        }
                    },
                    label = { Text("Minuto (0-59)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }*/

    }
}
