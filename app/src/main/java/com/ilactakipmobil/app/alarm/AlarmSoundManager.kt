package com.ilactakipmobil.app.alarm

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper

object AlarmSoundManager {

    private var ringtone: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoStopRunnable = Runnable { stop() }

    /**
     * Alarm sesini çalmaya başlar. 20 saniye sonra otomatik susacak şekilde zamanlanır.
     */
    fun play(context: Context, soundUri: Uri) {
        stop() // Varsa öncekini durdur

        try {
            ringtone = RingtoneManager.getRingtone(context.applicationContext, soundUri)
            ringtone?.play()

            // 20 saniye sonra çalmayı otomatik durdur (sonsuza kadar çalmaması için)
            handler.removeCallbacks(autoStopRunnable)
            handler.postDelayed(autoStopRunnable, 20_000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Çalan alarm sesini durdurur.
     */
    fun stop() {
        handler.removeCallbacks(autoStopRunnable)
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ringtone = null
        }
    }
}
