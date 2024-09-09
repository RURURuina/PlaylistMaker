package com.practicum.playlistmaker.mediateka.domain.db

import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackIdInPlaylist(playlist: Playlist, tracksId: ArrayList<Long>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
}