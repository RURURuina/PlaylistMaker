package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.share.domain.SharingInteractor
import com.practicum.playlistmaker.share.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(repository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
}