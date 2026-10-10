package com.ilactakipmobil.app.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ilactakipmobil.app.MainActivity
import com.ilactakipmobil.app.R
import com.ilactakipmobil.app.data.IlacRepository
import com.ilactakipmobil.app.db.DozKaydiEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_STOP_ALARM = "com.ilactakipmobil.app.ACTION_STOP_ALARM"
        const val ACTION_TAKE_DOSE = "com.ilactakipmobil.app.ACTION_TAKE_DOSE"
        const val ACTION_SKIP_DOSE = "com.ilactakipmobil.app.ACTION_SKIP_DOSE"
        const val ACTION_SNOOZE_DOSE = "com.ilactakipmobil.app.ACTION_SNOOZE_DOSE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ilacId = intent.getIntExtra("EXTRA_ILAC_ID", 0)
        val ilacAdi = intent.getStringExtra("EXTRA_ILAC_ADI") ?: "İlaç Vakti"
        val dozaj = intent.getStringExtra("EXTRA_DOZAJ") ?: "1 Doz"
        val saat = intent.getStringExtra("EXTRA_SAAT") ?: SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val bugunTarih = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val repo = IlacRepository(context.applicationContext)

        when (intent.action) {
            ACTION_TAKE_DOSE -> {
                AlarmSoundManager.stop()
                notificationManager.cancel(if (ilacId != 0) ilacId else 100)
                CoroutineScope(Dispatchers.IO).launch {
                    repo.updateDozDurumuByIlacAndSaat(ilacId, bugunTarih, saat, DozKaydiEntity.DURUM_ALINDI)
                }
                Toast.makeText(context, "✅ $ilacAdi alındı olarak kaydedildi.", Toast.LENGTH_SHORT).show()
                return
            }

            ACTION_SKIP_DOSE -> {
                AlarmSoundManager.stop()
                notificationManager.cancel(if (ilacId != 0) ilacId else 100)
                CoroutineScope(Dispatchers.IO).launch {
                    repo.updateDozDurumuByIlacAndSaat(ilacId, bugunTarih, saat, DozKaydiEntity.DURUM_ATLANDI)
                }
                Toast.makeText(context, "❌ $ilacAdi atlandı olarak kaydedildi.", Toast.LENGTH_SHORT).show()
                return
            }

            ACTION_SNOOZE_DOSE -> {
                AlarmSoundManager.stop()
                notificationManager.cancel(if (ilacId != 0) ilacId else 100)
                val ertelendiMinsLater = 10
                val ertelendiCalTime = System.currentTimeMillis() + (ertelendiMinsLater * 60 * 1000)
                val ertelendiSaatFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ertelendiCalTime))

                CoroutineScope(Dispatchers.IO).launch {
                    repo.updateDozDurumuByIlacAndSaat(ilacId, bugunTarih, saat, DozKaydiEntity.DURUM_ERTELENDI, ertelendiSaatFormat)
                }
                AlarmScheduler.scheduleSnoozeAlarm(context, ilacId, ilacAdi, dozaj, ertelendiMinsLater)
                return
            }

            ACTION_STOP_ALARM -> {
                AlarmSoundManager.stop()
                notificationManager.cancel(if (ilacId != 0) ilacId else 100)
                return
            }
        }

        val channelId = "ILAC_ALARM_CHANNEL_ID"
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                "İlaç Hatırlatma Alarmları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Tam vaktinde sesli ilaç hatırlatma bildirimi ve alarmı"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                setSound(alarmSound, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        AlarmSoundManager.play(context, alarmSound)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ilacId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Aldım" Butonu Intent
        val takeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TAKE_DOSE
            putExtra("EXTRA_ILAC_ID", ilacId)
            putExtra("EXTRA_ILAC_ADI", ilacAdi)
            putExtra("EXTRA_DOZAJ", dozaj)
            putExtra("EXTRA_SAAT", saat)
        }
        val takePendingIntent = PendingIntent.getBroadcast(
            context,
            ilacId + 10000,
            takeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Atladım" Butonu Intent
        val skipIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SKIP_DOSE
            putExtra("EXTRA_ILAC_ID", ilacId)
            putExtra("EXTRA_ILAC_ADI", ilacAdi)
            putExtra("EXTRA_DOZAJ", dozaj)
            putExtra("EXTRA_SAAT", saat)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            ilacId + 20000,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "10 Dk Ertele" Butonu Intent
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE_DOSE
            putExtra("EXTRA_ILAC_ID", ilacId)
            putExtra("EXTRA_ILAC_ADI", ilacAdi)
            putExtra("EXTRA_DOZAJ", dozaj)
            putExtra("EXTRA_SAAT", saat)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            ilacId + 30000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ İlaç Vakti: $ilacAdi")
            .setContentText("Dozaj: $dozaj - Saat: $saat")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("💊 İlacınızı almayı unutmayınız!\n\nİlaç: $ilacAdi\nDozaj: $dozaj\nZaman: $saat")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .addAction(0, "✅ Aldım", takePendingIntent)
            .addAction(0, "❌ Atladım", skipPendingIntent)
            .addAction(0, "⏰ 10 Dk Ertele", snoozePendingIntent)
            .build()

        notificationManager.notify(if (ilacId != 0) ilacId else 100, notification)
    }
}
