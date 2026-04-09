package com.example.pastillas.ui.notificaciones


import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pastillas.R
import com.example.pastillas.ui.main.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ALARM", "RECEIVER ACTIVADO")

        val nombre = intent.getStringExtra("nombre") ?: "Toma"
        val tomaId = intent.getIntExtra("tomaId", 0)
        val usarAlarma = intent.getBooleanExtra("usarAlarma", true)

        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("nombre", nombre)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val contentIntent = if (usarAlarma) activityIntent else mainIntent

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            tomaId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (usarAlarma) "alarmas" else "recordatorios"
        val alarmSound: Uri = Settings.System.DEFAULT_ALARM_ALERT_URI
        val notificationSound: Uri = Settings.System.DEFAULT_NOTIFICATION_URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = if (usarAlarma) {
                android.app.NotificationChannel(
                    channelId,
                    "Alarmas",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones de alarmas de tomas"
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                    setSound(
                        alarmSound,
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                }
            } else {
                android.app.NotificationChannel(
                    channelId,
                    "Recordatorios",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notificaciones de recordatorios de tomas"
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                    setSound(
                        notificationSound,
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                }
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                Log.w("ALARM", "POST_NOTIFICATIONS no concedido; intentando abrir actividad.")
                context.startActivity(activityIntent)
                return
            }
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Hora de tu toma")
            .setContentText(nombre)
            .setPriority(if (usarAlarma) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(if (usarAlarma) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(fullScreenPendingIntent)

        if (usarAlarma) {
            builder
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSound(alarmSound)
        } else {
            builder.setSound(notificationSound)
        }

        val notification = builder.build()

        NotificationManagerCompat.from(context).notify(tomaId.coerceAtLeast(1), notification)
    }
}
