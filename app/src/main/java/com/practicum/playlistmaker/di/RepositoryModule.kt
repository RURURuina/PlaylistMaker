package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.share.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(iTunesService = get(), searchHistory = get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(mediaPlayer = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(context = get())
    }
}