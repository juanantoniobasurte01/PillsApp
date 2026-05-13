package com.example.pastillas.ui.tomas

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pastillas.ui.components.cards.CardTomaIniciar
import com.example.pastillas.ui.viewmodel.TomaViewModel

@Composable
fun IniciarTomaScreen(
    navController: NavController,
    viewModel: TomaViewModel = viewModel()
) {
    val tomas = viewModel.tomasDisponibles

    if (tomas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay tomas registradas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }




    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        itemsIndexed(tomas) { index, toma ->
            CardTomaIniciar(
                toma = toma,
                index = index,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
