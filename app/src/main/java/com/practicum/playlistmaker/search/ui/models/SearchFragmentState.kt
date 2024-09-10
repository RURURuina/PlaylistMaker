package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchFragmentState {

    object Loading: SearchFragmentState

    object Empty: SearchFragmentState

    data class Content (
        val tracks: MutableList<Track?>
    ) : SearchFragmentState

    data class Error(
        val errorMessage: String
    ) : SearchFragmentState

    data class History(
        val tracks: MutableList<Track?>
    ) : SearchFragmentState
}