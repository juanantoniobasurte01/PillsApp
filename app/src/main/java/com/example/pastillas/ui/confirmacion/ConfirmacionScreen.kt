package com.example.pastillas.ui.confirmacion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastillas.ui.components.botones.BotonConfirmarRepetir
import com.example.pastillas.data.model.Toma

@Composable
fun ConfirmacionScreen(
    navController: NavController,
    toma: Toma,
    pastillasDetectadas: Int,
    indexToma: Int,
    onGuardarHistorial: (Toma, Int, Boolean) -> Unit
) {
    val correcta = pastillasDetectadas == toma.numeroPastillas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (correcta) "DOSIS CORRECTA" else "DOSIS ERRÓNEA",
            color = if (correcta) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Pastillas detectadas: $pastillasDetectadas",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        BotonConfirmarRepetir(
            dosisCorrecta = correcta,
            toma = toma,
            pastillasDetectadas = pastillasDetectadas,
            indexToma = indexToma,
            navController = navController,
            onGuardarHistorial = onGuardarHistorial,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
