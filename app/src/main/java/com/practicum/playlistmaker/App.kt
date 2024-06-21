package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor


class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor
    var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        val settingsInteractor: SettingsInteractor =
            Creator.provideSettingsInteractor(applicationContext)
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
        themeSwitcher(darkTheme)
    }

    fun themeSwitcher(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}