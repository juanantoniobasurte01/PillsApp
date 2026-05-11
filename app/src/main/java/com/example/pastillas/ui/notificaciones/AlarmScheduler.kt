package com.example.pastillas.ui.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.data.model.Toma
import com.example.pastillas.utils.TimeUtils
import kotlinx.coroutines.flow.first
import java.util.Calendar

object AlarmScheduler {

    suspend fun programarToma(context: Context, toma: Toma) {
        if (!toma.notificacionActiva) return

        val settings = SettingsDataStore(context)
        val modoPruebas = settings.modoPruebasFlow.first()
        val (hora, minuto) = if (modoPruebas) {
            val horaPruebas = settings.horaPruebasFlow.first()
            val minutoPruebas = settings.minutoPruebasFlow.first()
            Pair(horaPruebas, minutoPruebas)
        } else {
            TimeUtils.obtenerHora(toma.horario)
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        Log.d("ALARM", "ENTRA A programarToma")
        Log.d("ALARM", "TIME NOW: ${System.currentTimeMillis()}")
        Log.d("ALARM", "TIME SET: ${calendar.timeInMillis}")
        Log.d("ALARM", "DIFF MIN: ${(calendar.timeInMillis - System.currentTimeMillis()) / 60000}")

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("nombre", toma.nombre)
            putExtra("tomaId", toma.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            toma.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.d("ALARM", "Programando alarma para: ${calendar.timeInMillis}")
        val triggerAtMillis = calendar.timeInMillis
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms()
                ) {
                    Log.w("ALARM", "Exact alarm not permitted; using setAndAllowWhileIdle.")
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    Log.d("ALARM", "Using setExactAndAllowWhileIdle.")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                Log.d("ALARM", "Using set (inexact).")
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (se: SecurityException) {
            Log.e("ALARM", "Exact alarm permission missing; using while-idle fallback.", se)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelarToma(context: Context, tomaId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tomaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        NotificationManagerCompat.from(context).cancel(tomaId.coerceAtLeast(1))
    }
}
