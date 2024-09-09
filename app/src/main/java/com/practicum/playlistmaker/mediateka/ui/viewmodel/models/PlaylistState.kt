package com.practicum.playlistmaker.mediateka.ui.viewmodel.models

import com.practicum.playlistmaker.mediateka.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(
        val playlist: List<Playlist>
    ) : PlaylistState

    object Empty: PlaylistState
}