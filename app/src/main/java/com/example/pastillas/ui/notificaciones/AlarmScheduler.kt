package com.example.pastillas.ui.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pastillas.data.SettingsDataStore
import com.example.pastillas.data.model.Toma
import com.example.pastillas.ui.notificaciones.AlarmActivity
import com.example.pastillas.utils.TimeUtils
import kotlinx.coroutines.flow.first
import java.util.Calendar
import android.os.Build

object AlarmScheduler {

    suspend fun programarToma(context: Context, toma: Toma) {

        if (!toma.notificacionActiva) return

        val settings = SettingsDataStore(context)
        val modoPruebas = settings.modoPruebasFlow.first()
        val usarAlarma = settings.modoNotificacionFlow.first()
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


        // Si la hora ya pasó hoy, programa para el día siguiente
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("nombre", toma.nombre)
            putExtra("tomaId", toma.id)
            putExtra("usarAlarma", usarAlarma)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            toma.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("nombre", toma.nombre)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            toma.id,
            showIntent,
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
                    if (usarAlarma) {
                        Log.w("ALARM", "Exact alarm not permitted; using setAlarmClock.")
                        val info = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
                        alarmManager.setAlarmClock(info, pendingIntent)
                    } else {
                        Log.w("ALARM", "Exact alarm not permitted; using set (notification mode).")
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                        )
                    }
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
            Log.e("ALARM", "Exact alarm permission missing; using inexact alarm.", se)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}
