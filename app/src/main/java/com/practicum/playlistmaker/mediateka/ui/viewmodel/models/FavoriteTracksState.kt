package com.practicum.playlistmaker.mediateka.ui.viewmodel.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {
    data class Content(
        val favoriteTracks: List<Track>
    ) : FavoriteTracksState

    object Empty: FavoriteTracksState
}