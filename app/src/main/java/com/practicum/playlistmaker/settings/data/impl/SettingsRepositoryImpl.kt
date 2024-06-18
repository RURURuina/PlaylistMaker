package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {

    companion object {
        const val NIGHT_THEME_KEY = "night_key"
    }

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefs.getBoolean(NIGHT_THEME_KEY, false))
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPrefs.edit()
            .putBoolean(NIGHT_THEME_KEY, settings.darkTheme)
            .apply()
    }
}