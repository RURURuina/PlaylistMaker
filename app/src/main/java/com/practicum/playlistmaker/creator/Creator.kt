package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.share.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.SharingInteractor
import com.practicum.playlistmaker.share.domain.impl.SharingInteractorImpl


object Creator {

    const val THEME = "playlistmaker_theme"
    private const val SEARCH_HISTORY = "SearchHistory"
    fun mediaPlayerCreator(): MediaPlayerInteractor {

        val mediaPlayer = MediaPlayer()
        val repository: MediaPlayerRepository = MediaPlayerRepositoryImpl(mediaPlayer)
        return MediaPlayerInteractorImpl(repository)
    }

    fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences(
                THEME, MODE_PRIVATE
            )
        )

    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
    }
}