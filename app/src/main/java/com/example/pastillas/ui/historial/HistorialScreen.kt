package com.example.pastillas.ui.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pastillas.data.model.HistorialEntry
import com.example.pastillas.ui.components.cards.CardHistorial

@Composable
fun HistorialScreen(
    historial: List<HistorialEntry>
) {
    if (historial.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay historial todavia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(historial, key = { it.id }) { entrada ->
            CardHistorial(entrada = entrada)
        }
    }
}
