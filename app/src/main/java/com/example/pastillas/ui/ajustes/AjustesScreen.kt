package com.example.pastillas.ui.ajustes

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.data.SettingsDefaults
import com.example.pastillas.ui.components.botones.BotonDialogo
import com.example.pastillas.ui.components.botones.PillSwitch
import com.example.pastillas.ui.viewmodel.TomaViewModel
import kotlinx.coroutines.launch

@Composable
fun AjustesScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    viewModel: TomaViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val settings = remember { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()

    val persistedModoTerceraEdad by settings.modoTerceraEdadFlow.collectAsState(
        initial = SettingsDefaults.MODO_TERCERA_EDAD
    )
    val modoPruebas by settings.modoPruebasFlow.collectAsState(
        initial = SettingsDefaults.MODO_PRUEBAS
    )
    val horaPruebas by settings.horaPruebasFlow.collectAsState(
        initial = SettingsDefaults.HORA_PRUEBAS
    )
    val minutoPruebas by settings.minutoPruebasFlow.collectAsState(
        initial = SettingsDefaults.MINUTO_PRUEBAS
    )
    val minutosDesdeAhoraPruebas by settings.minutosDesdeAhoraPruebasFlow.collectAsState(
        initial = SettingsDefaults.MINUTOS_DESDE_AHORA_PRUEBAS
    )

    var terceraEdad by rememberSaveable {
        mutableStateOf(SettingsDefaults.MODO_TERCERA_EDAD)
    }
    var horaInput by remember(horaPruebas) { mutableStateOf(horaPruebas.toString()) }
    var minutoInput by remember(minutoPruebas) { mutableStateOf(minutoPruebas.toString()) }

    LaunchedEffect(persistedModoTerceraEdad) {
        terceraEdad = persistedModoTerceraEdad
    }

    var notificationsEnabled by remember { mutableStateOf(false) }
    var exactAlarmsAllowed by remember { mutableStateOf(true) }

    fun refreshPermissionState() {
        notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        exactAlarmsAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager?.canScheduleExactAlarms() == true
        } else {
            true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        refreshPermissionState()
    }

    LaunchedEffect(Unit) {
        refreshPermissionState()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissionState()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                checked = isDarkMode,
                onCheckedChange = { value ->
                    onDarkModeChange(value)
                }
            )
        }

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
                onCheckedChange = { value ->
                    terceraEdad = value
                    scope.launch {
                        settings.guardarModoTerceraEdad(value)
                    }
                }
            )
        }

        if (!notificationsEnabled || !exactAlarmsAllowed) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsEnabled) {
                    BotonDialogo(
                        text = "PERMITIR NOTIFICACIONES",
                        onClick = {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        width = 300.dp,
                        height = 60.dp
                    )
                }

                if (!notificationsEnabled) {
                    BotonDialogo(
                        text = "AJUSTES NOTIFICACIONES",
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    putExtra(Settings.EXTRA_CHANNEL_ID, "recordatorios")
                                }
                                context.startActivity(intent)
                            } else {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            }
                        },
                        width = 300.dp,
                        height = 60.dp
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !exactAlarmsAllowed) {
                    BotonDialogo(
                        text = "PERMITIR RECORDATORIOS",
                        onClick = {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        },
                        width = 300.dp,
                        height = 60.dp
                    )
                }
            }
        }





        //MODO PRUEBAS (comentar luego)







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
                    scope.launch {
                        settings.guardarModoPruebas(value)
                        viewModel.reprogramarTomas(context)
                    }
                }
            )
        }

        if (modoPruebas) {
            Text(
                text = "Anadir tiempo rapido",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(1, 3, 5).forEach { minutos ->
                    Button(
                        onClick = {
                            scope.launch {
                                settings.guardarMinutosDesdeAhoraPruebas(minutos)
                                viewModel.reprogramarTomas(context)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+$minutos min")
                    }
                }
            }

            if (minutosDesdeAhoraPruebas > 0) {
                Text(
                    text = "Prueba rapida activa: +$minutosDesdeAhoraPruebas min.",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

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
                            scope.launch {
                                settings.guardarMinutosDesdeAhoraPruebas(0)
                                settings.guardarHoraPruebas(hora)
                                if (modoPruebas) {
                                    viewModel.reprogramarTomas(context)
                                }
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
                            scope.launch {
                                settings.guardarMinutosDesdeAhoraPruebas(0)
                                settings.guardarMinutoPruebas(minuto)
                                if (modoPruebas) {
                                    viewModel.reprogramarTomas(context)
                                }
                            }
                        }
                    },
                    label = { Text("Minuto (0-59)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            if (minutosDesdeAhoraPruebas > 0) {
                Button(
                    onClick = {
                        scope.launch {
                            settings.guardarMinutosDesdeAhoraPruebas(0)
                            viewModel.reprogramarTomas(context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a hora fija")
                }
            }
        }

        */










    }
}
