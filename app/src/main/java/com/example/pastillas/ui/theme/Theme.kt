package com.example.pastillas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),    // azul claro para elementos primarios
    secondary = Color(0xFFCE93D8),  // secundario morado
    tertiary = Color(0xFFF48FB1),   // terciario rosa
    background = Color(0xFF252525), // fondo oscuro
    surface = Color(0xFF2A2A2A),    // superficies más claras que el fondo
    onPrimary = Color.Black,         // texto sobre elementos primarios
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,      // Texto sobre fondo oscuro
    onSurface = Color.White )         // texto sobre superficies oscuras

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF9C27B0),
    tertiary = Color(0xFFE91E63),
    background = Color(0xFFF8F6FD), // gris claro
    surface = Color(0xFFFFFFFF),    //blanco
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,     // texto sobre fondo claro
    onSurface = Color.Black         // texto sobre superficies claras
)

@Composable
fun PastillasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),


    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
