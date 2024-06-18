package com.practicum.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.share.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private var themeLiveData = MutableLiveData(true)

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().darkTheme
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                    SettingsViewModel(
                        Creator.provideSettingsInteractor(application),
                        Creator.provideSharingInteractor(application)

                    )
                }
            }
    }

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData

    fun shareAppButton() {
        sharingInteractor.shareApp()
    }

    fun openTermsButton() {
        sharingInteractor.openTerms()
    }

    fun contactSupportButton() {
        sharingInteractor.contactSupport()
    }

    fun clickSwitchTheme(isChecked: Boolean) {
        themeLiveData.value = isChecked
        settingsInteractor.updateThemeSettings(isChecked)
        switchTheme(isChecked)
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}