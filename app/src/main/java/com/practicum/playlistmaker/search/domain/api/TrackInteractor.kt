package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    suspend fun searchTracks(term: String): Flow<Result<List<Track>>>
    fun getSearchHistory(): List<Track?>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}