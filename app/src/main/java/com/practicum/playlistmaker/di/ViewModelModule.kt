package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.mediateka.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.NewPlaylistViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistEditViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistInfoViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<SearchViewModel> {
        SearchViewModel(interactor = get())
    }

    viewModel<MediaPlayerViewModel>() {
        MediaPlayerViewModel(
            interactor = get(),
            favoriteTrackInteractor = get(),
            playlistInteractor = get()
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(settingsInteractor = get(), sharingInteractor = get())
    }

    viewModel<FavoritesViewModel>() {
        FavoritesViewModel(favoriteTrackInteractor = get())
    }

    viewModel<PlaylistsViewModel>() {
        PlaylistsViewModel(playlistInteractor = get())
    }

    viewModel {
        NewPlaylistViewModel(localStorageInteractor = get(), playlistInteractor = get())
    }

    viewModel {
        PlaylistInfoViewModel(playlistsInteractor = get(), sharingInteractor = get())
    }

    viewModel { (playlist: Playlist) ->
        PlaylistEditViewModel(localStorageInteractor = get(), playlistInteractor = get(), playlist)
    }
}