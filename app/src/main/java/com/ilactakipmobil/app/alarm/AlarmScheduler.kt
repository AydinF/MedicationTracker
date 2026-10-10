package com.ilactakipmobil.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ilactakipmobil.app.MainActivity
import com.ilactakipmobil.app.model.Ilac
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AlarmScheduler {

    /**
     * İlaç için belirtilen tam saatte AlarmManager (setAlarmClock) ile kesin alarm kurar.
     */
    fun scheduleAlarm(context: Context, ilac: Ilac) {
        if (!ilac.aktifMi) return

        val parts = ilac.saat.split(":")
        if (parts.size < 2) return

        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val now = System.currentTimeMillis()

        var calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Eğer 12 saatlik format girilmişse (Örn: 22:41 iken 10:43 girilmesi), PM saatine uyarla (22:43)
        if (calendar.timeInMillis <= now && hour < 12) {
            val pmCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour + 12)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (pmCalendar.timeInMillis > now) {
                calendar = pmCalendar
            }
        }

        // Eğer saat tamamen geçmişse, alarmı yarına kur
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ILAC_ID", ilac.id)
            putExtra("EXTRA_ILAC_ADI", ilac.ilacAdi)
            putExtra("EXTRA_DOZAJ", ilac.dozaj)
            putExtra("EXTRA_SAAT", ilac.saat)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ilac.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            ilac.id,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            val format = SimpleDateFormat("HH:mm (dd MMM)", Locale.getDefault())
            val zamanStr = format.format(calendar.time)
            Toast.makeText(context, "⏰ Alarm Kuruldu: $zamanStr - ${ilac.ilacAdi}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Erteleme için geçici alarm kurar (10 dk sonra)
     */
    fun scheduleSnoozeAlarm(context: Context, ilacId: Int, ilacAdi: String, dozaj: String, dakikaSonra: Int = 10) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (dakikaSonra * 60 * 1000)
        val ertelendiSaatFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(triggerTime))

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ILAC_ID", ilacId)
            putExtra("EXTRA_ILAC_ADI", ilacAdi)
            putExtra("EXTRA_DOZAJ", dozaj)
            putExtra("EXTRA_SAAT", ertelendiSaatFormat)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ilacId + 99000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Toast.makeText(context, "⏰ Alarm $dakikaSonra dakika sonraya ertelendi ($ertelendiSaatFormat)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Kurulu olan alarmı iptal eder.
     */
    fun cancelAlarm(context: Context, ilacId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ilacId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
