package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import mediateka.ui.viewmodel.FavoritesViewModel
import mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<SearchViewModel> {
        SearchViewModel(interactor = get())
    }

    viewModel<MediaPlayerViewModel>() {
        MediaPlayerViewModel(interactor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(settingsInteractor = get(), sharingInteractor = get())
    }

    viewModel<FavoritesViewModel>()

    viewModel<PlaylistsViewModel>()
}