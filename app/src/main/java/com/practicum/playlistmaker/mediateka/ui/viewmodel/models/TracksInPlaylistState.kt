package com.practicum.playlistmaker.mediateka.ui.viewmodel.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TracksInPlaylistState {
    data class Content(
        val tracksInPlaylist: List<Track>
    ) : TracksInPlaylistState

    object Empty : TracksInPlaylistState
}