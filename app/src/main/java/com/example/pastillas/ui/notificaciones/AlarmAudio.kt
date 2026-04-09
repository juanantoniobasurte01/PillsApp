package com.example.pastillas.ui.notificaciones

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

object AlarmAudio {
    private var mediaPlayer: MediaPlayer? = null

    @Synchronized
    fun start(context: Context) {
        if (mediaPlayer?.isPlaying == true) return
        val mp = MediaPlayer.create(context.applicationContext, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mp.isLooping = true
        mp.start()
        mediaPlayer = mp
    }

    @Synchronized
    fun stop() {
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) {
                    mp.stop()
                }
            } catch (_: Exception) {
            } finally {
                mp.release()
            }
        }
        mediaPlayer = null
    }
}
