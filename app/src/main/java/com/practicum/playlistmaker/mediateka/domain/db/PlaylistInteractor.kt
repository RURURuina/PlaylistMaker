package com.practicum.playlistmaker.mediateka.domain.db

import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackIdInPlaylist(playlist: Playlist, tracksId: List<Long>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylistById(playlistId: Int): Flow<Playlist?>
    fun getTracksInPlaylistById(tracksIdInPlaylist: List<Long>): Flow<List<Track>>
    suspend fun deleteTrackInPlaylist(playlist: Playlist, trackId: Long)
    suspend fun deletePlaylist(playlist: Playlist)
}