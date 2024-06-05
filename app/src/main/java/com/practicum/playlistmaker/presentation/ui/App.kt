package com.practicum.playlistmaker.presentation.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {


    companion object {
        private const val THEME_KEY = "theme_key"
        private const val THEME_FILE_NAME = "theme"
        private const val SWITCH_STATE_KEY = "switch_key"
    }

    var themeSwitchState = false
    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE)
        val darkThemeEnabled = sharedPreferences.getBoolean(THEME_KEY, isSystemDarkTheme())
        switchTheme(darkThemeEnabled)
        themeSwitchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
    }

    private fun isSystemDarkTheme(): Boolean {
        val systemDefaultNightMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return systemDefaultNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun saveTheme(darkThemeEnabled: Boolean) {
        switchTheme(darkThemeEnabled)
        val sharedPreferences = getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()
    }

    fun saveSwitchState(checked: Boolean) {
        themeSwitchState = checked
        val sharedPreferences = getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, checked).apply()
    }

    fun setSwitchState(checked: Boolean) {
        themeSwitchState = checked
    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}