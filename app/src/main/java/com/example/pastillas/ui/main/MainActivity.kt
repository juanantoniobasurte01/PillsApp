package com.example.pastillas.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pastillas.R
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.data.SettingsDefaults
import com.example.pastillas.data.model.Toma
import com.example.pastillas.ui.ajustes.AjustesScreen
import com.example.pastillas.ui.camara.CamaraScreen
import com.example.pastillas.ui.components.botones.BotonHistorial
import com.example.pastillas.ui.components.botones.BotonIniciarToma
import com.example.pastillas.ui.components.botones.BotonTomasDisponibles
import com.example.pastillas.ui.confirmacion.ConfirmacionScreen
import com.example.pastillas.ui.historial.HistorialScreen
import com.example.pastillas.ui.theme.PastillasTheme
import com.example.pastillas.ui.tomas.IniciarTomaScreen
import com.example.pastillas.ui.tomas.RegistroTomaScreen
import com.example.pastillas.ui.tomas.TomasDisponiblesScreen
import com.example.pastillas.ui.viewmodel.TomaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onStart() {
        super.onStart()
        NotificationManagerCompat.from(this).cancelAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings = remember { SettingsDataStore(applicationContext) }
            val persistedDarkMode by settings.modoOscuroFlow.collectAsState(
                initial = SettingsDefaults.MODO_OSCURO
            )
            val modoTerceraEdad by settings.modoTerceraEdadFlow.collectAsState(
                initial = SettingsDefaults.MODO_TERCERA_EDAD
            )
            val scope = rememberCoroutineScope()
            var isDarkMode by remember { mutableStateOf(SettingsDefaults.MODO_OSCURO) }

            LaunchedEffect(persistedDarkMode) {
                isDarkMode = persistedDarkMode
            }

            PastillasTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val tomaViewModel: TomaViewModel = viewModel()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val tomas = remember { mutableStateListOf<Toma>() }
                val historial = remember { mutableStateListOf<Triple<Toma, Int, Boolean>>() }

                val backgroundPainter = if (isDarkMode) {
                    painterResource(id = R.drawable.dark_background)
                } else {
                    painterResource(id = R.drawable.light_background)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = backgroundPainter,
                        contentDescription = "Fondo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text("PillApp (provisional)") },
                                navigationIcon = {
                                    if (currentRoute != "main") {
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Atras"
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { navController.navigate("ajustes") }) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Ajustes"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent
                                )
                            )
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "main",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("tomas_disponibles") {
                                TomasDisponiblesScreen(
                                    navController = navController,
                                    viewModel = tomaViewModel,
                                    isDarkMode = isDarkMode
                                )
                            }
                            composable("registro_toma") {
                                RegistroTomaScreen(
                                    navController = navController,
                                    viewModel = tomaViewModel
                                )
                            }
                            composable(
                                "registro_toma/{tomaId}",
                                arguments = listOf(navArgument("tomaId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val tomaId = backStackEntry.arguments?.getInt("tomaId") ?: -1
                                val tomaToEdit = tomaViewModel.tomasDisponibles.firstOrNull { it.id == tomaId }
                                RegistroTomaScreen(
                                    navController = navController,
                                    viewModel = tomaViewModel,
                                    tomaToEdit = tomaToEdit
                                )
                            }
                            composable("historial") {
                                HistorialScreen(navController = navController, historial = historial)
                            }
                            composable("ajustes") {
                                AjustesScreen(
                                    navController = navController,
                                    isDarkMode = isDarkMode,
                                    onDarkModeChange = { value ->
                                        isDarkMode = value
                                        scope.launch {
                                            settings.guardarModoOscuro(value)
                                        }
                                    },
                                    viewModel = tomaViewModel
                                )
                            }
                            composable("main") {
                                MainScreen(
                                    navController = navController,
                                    tomas = tomas,
                                    modoTerceraEdad = modoTerceraEdad
                                )
                            }
                            composable("iniciar_toma") {
                                IniciarTomaScreen(
                                    navController = navController,
                                    viewModel = tomaViewModel
                                )
                            }
                            composable(
                                "camara/{tomaIndex}",
                                arguments = listOf(navArgument("tomaIndex") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val indexToma = backStackEntry.arguments?.getInt("tomaIndex") ?: -1
                                CamaraScreen(
                                    navController = navController,
                                    indexToma = indexToma,
                                    viewModel = tomaViewModel
                                )
                            }
                            composable(
                                "confirmacion/{tomaIndex}/{pastillasDetectadas}",
                                arguments = listOf(
                                    navArgument("tomaIndex") { type = NavType.IntType },
                                    navArgument("pastillasDetectadas") { type = NavType.IntType }
                                )
                            ) { backStackEntry ->
                                val indexToma = backStackEntry.arguments?.getInt("tomaIndex") ?: 0
                                val pastillasDetectadas = backStackEntry.arguments?.getInt("pastillasDetectadas") ?: 0

                                val tomasDisponibles = tomaViewModel.tomasDisponibles
                                if (indexToma !in tomasDisponibles.indices) return@composable
                                val toma = tomasDisponibles[indexToma]

                                ConfirmacionScreen(
                                    navController = navController,
                                    toma = toma,
                                    indexToma = indexToma,
                                    pastillasDetectadas = pastillasDetectadas,
                                    onGuardarHistorial = { t, detectadas, correcta ->
                                        historial.add(Triple(t, detectadas, correcta))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    tomas: List<Toma>,
    modoTerceraEdad: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BotonIniciarToma(
            text = "INICIAR TOMA",
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("listaTomas", tomas)
                navController.navigate("iniciar_toma")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(100.dp))

        if (modoTerceraEdad) {
            BotonIniciarToma(
                text = stringResource(R.string.senior_help_button),
                onClick = {
                    context.startActivity(Intent(context, ComoUsarLaAppScreen::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BotonTomasDisponibles(
                    text = "TOMAS",
                    onClick = { navController.navigate("tomas_disponibles") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                BotonHistorial(
                    text = "HISTORIAL",
                    onClick = { navController.navigate("historial") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
