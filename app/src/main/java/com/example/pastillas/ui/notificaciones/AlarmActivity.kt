package com.example.pastillas.ui.notificaciones

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.R
import android.os.Build


class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar encima de pantalla bloqueada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val nombre = intent.getStringExtra("nombre") ?: "Toma"

        // Sonido alarma
        AlarmAudio.start(this)

        setContent {
            AlarmScreen(
                nombre = nombre,
                onStop = {
                    AlarmAudio.stop()
                    finish()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmAudio.stop()
    }
}
