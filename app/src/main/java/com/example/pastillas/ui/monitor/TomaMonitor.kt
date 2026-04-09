package com.example.pastillas.ui.monitor

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.example.pastillas.ui.notificaciones.AlarmActivity
import com.example.pastillas.ui.viewmodel.TomaViewModel
import com.example.pastillas.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

object TomaMonitor {

    fun iniciar(viewModel: TomaViewModel, context: Context) {

        viewModel.viewModelScope.launch {

            while (true) {

                val ahora = Calendar.getInstance()

                val horaActual = ahora.get(Calendar.HOUR_OF_DAY)
                val minutoActual = ahora.get(Calendar.MINUTE)

                viewModel.tomasDisponibles.forEach { toma ->

                    val (hora, minuto) = TimeUtils.obtenerHora(toma.horario)

                    if (horaActual == hora && minutoActual == minuto) {

                        // LAnzamiento de alarma
                        val intent = Intent(context, AlarmActivity::class.java).apply {
                            putExtra("nombre", toma.nombre)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        context.startActivity(intent)
                    }
                }

                delay(60000) // revisa cada minuto
            }
        }
    }
}