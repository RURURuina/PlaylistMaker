package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    fun searchTracks(term: String, callback: (Result<MutableList<Track?>>) -> Unit)
    fun getSearchHistory(): MutableList<Track?>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}