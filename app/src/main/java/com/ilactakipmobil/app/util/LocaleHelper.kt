package com.ilactakipmobil.app.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "app_language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "tr") ?: "tr"
    }

    fun setLanguage(context: Context, languageTag: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageTag).apply()

        val appLocales = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocales)
    }

    fun applyLanguage(context: Context): Context {
        val lang = getLanguage(context)
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    fun formatDozajToCurrentLocale(dozaj: String, context: Context): String {
        if (dozaj.isEmpty()) return ""
        val lang = getLanguage(context)
        return if (lang == "en") {
            dozaj.replace("Doz", "Dose", ignoreCase = true)
                 .replace("Her Gün", "Everyday", ignoreCase = true)
        } else {
            dozaj.replace("Dose", "Doz", ignoreCase = true)
                 .replace("Everyday", "Her Gün", ignoreCase = true)
        }
    }

    fun formatGunlerToCurrentLocale(gunler: List<String>, context: Context): String {
        if (gunler.isEmpty()) return ""
        return gunler.flatMap { it.split(",") }.map { g ->
            when (g.trim()) {
                "Her Gün", "Everyday" -> context.getString(com.ilactakipmobil.app.R.string.day_everyday)
                "Pzt", "Mon" -> context.getString(com.ilactakipmobil.app.R.string.day_mon)
                "Sal", "Tue" -> context.getString(com.ilactakipmobil.app.R.string.day_tue)
                "Çar", "Wed" -> context.getString(com.ilactakipmobil.app.R.string.day_wed)
                "Per", "Thu" -> context.getString(com.ilactakipmobil.app.R.string.day_thu)
                "Cum", "Fri" -> context.getString(com.ilactakipmobil.app.R.string.day_fri)
                "Cmt", "Sat" -> context.getString(com.ilactakipmobil.app.R.string.day_sat)
                "Paz", "Sun" -> context.getString(com.ilactakipmobil.app.R.string.day_sun)
                else -> g.trim()
            }
        }.distinct().joinToString(", ")
    }
}
