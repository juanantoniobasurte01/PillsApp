package com.example.pastillas.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pastillas.R
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.data.SettingsDefaults
import com.example.pastillas.ui.components.botones.BotonIniciarToma
import com.example.pastillas.ui.theme.PastillasTheme

class ComoUsarLaAppScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings = remember { SettingsDataStore(applicationContext) }
            val isDarkMode by settings.modoOscuroFlow.collectAsState(
                initial = SettingsDefaults.MODO_OSCURO
            )

            PastillasTheme(darkTheme = isDarkMode) {
                SeniorHelpScreen(
                    isDarkMode = isDarkMode,
                    onClose = { finish() }
                )
            }
        }
    }
}

@Composable
private fun SeniorHelpScreen(
    isDarkMode: Boolean,
    onClose: () -> Unit
) {
    val backgroundPainter = if (isDarkMode) {
        painterResource(id = R.drawable.dark_background)
    } else {
        painterResource(id = R.drawable.light_background)
    }

    val instructionImages = if (isDarkMode) {
        listOf(
            R.drawable.instruccion_dark_1,
            R.drawable.instruccion_dark_2,
            R.drawable.instruccion_dark_3,
            R.drawable.instruccion_dark_4,
            R.drawable.instruccion_dark_5
        )
    } else {
        listOf(
            R.drawable.instruccion_1,
            R.drawable.instruccion_2,
            R.drawable.instruccion_3,
            R.drawable.instruccion_4,
            R.drawable.instruccion_5
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.senior_help_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }

                    items(instructionImages) { imageRes ->
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = stringResource(R.string.senior_help_title),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                BotonIniciarToma(
                    text = stringResource(R.string.senior_help_close),
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
