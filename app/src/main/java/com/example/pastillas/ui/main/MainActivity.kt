package com.example.pastillas.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pastillas.ui.theme.PastillasTheme
import com.example.pastillas.ui.ajustes.AjustesScreen
import com.example.pastillas.ui.camara.CamaraScreen
import com.example.pastillas.ui.components.botones.BotonHistorial
import com.example.pastillas.ui.components.botones.BotonIniciarToma
import com.example.pastillas.ui.components.botones.BotonTomasDisponibles
import com.example.pastillas.ui.confirmacion.ConfirmacionScreen
import com.example.pastillas.ui.historial.HistorialScreen
import com.example.pastillas.ui.tomas.IniciarTomaScreen
import com.example.pastillas.ui.tomas.RegistroTomaScreen
import com.example.pastillas.data.model.Toma
import com.example.pastillas.ui.tomas.TomasDisponiblesScreen
import com.example.pastillas.R
import com.example.pastillas.ui.viewmodel.TomaViewModel


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(false) }

            PastillasTheme(darkTheme = isDarkMode.value) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val tomas = remember { mutableStateListOf<Toma>() }
                val historial = remember { mutableStateListOf<Triple<Toma, Int, Boolean>>() }

                // Fondo segun el modo claro o oscuro
                val backgroundPainter = if (isDarkMode.value) {
                    painterResource(id = R.drawable.dark_background)
                } else {
                    painterResource(id = R.drawable.light_background)
                }

                // Contenedor principal
                Box(modifier = Modifier.fillMaxSize()) {

                    // fondo
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
                                                contentDescription = "Atrás"
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


                        // Contenido
                        NavHost(
                            navController = navController,
                            startDestination = "main",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("tomas_disponibles") {
                                TomasDisponiblesScreen(navController = navController, isDarkMode = isDarkMode)
                            }
                            composable("registro_toma") {
                                val viewModel: TomaViewModel = viewModel()
                                RegistroTomaScreen(navController = navController, viewModel = viewModel)
                            }
                            composable("historial") {
                                HistorialScreen(navController = navController, historial = historial)
                            }
                            composable("ajustes") {
                                AjustesScreen(navController = navController, isDarkMode)
                            }
                            composable("main") {
                                MainScreen(navController = navController, tomas = tomas)
                            }
                            composable("iniciar_toma") {
                                IniciarTomaScreen(navController = navController)
                            }
                            composable(
                                "camara/{tomaIndex}",
                                arguments = listOf(navArgument("tomaIndex") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val indexToma = backStackEntry.arguments?.getInt("tomaIndex") ?: -1
                                CamaraScreen(navController = navController, indexToma = indexToma)
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


                                val viewModel: TomaViewModel = viewModel()
                                val tomas = viewModel.tomasDisponibles
                                if (indexToma !in tomas.indices) return@composable
                                val toma = tomas[indexToma]

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
    fun MainScreen(navController: NavController, tomas: List<Toma>, modifier: Modifier = Modifier) {
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







