package com.practicum.playlistmaker.mediateka.domain.impl

import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist, tracksId: List<Long>, track: Track
    ) {
        playlistRepository.addTrackIdInPlaylist(playlist, tracksId as ArrayList<Long>, track)
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getSavedPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksInPlaylistById(tracksIdInPlaylist: List<Long>): Flow<List<Track>> {
        return playlistRepository.getTracksInPlaylistById(tracksIdInPlaylist)
    }

    override suspend fun deleteTrackInPlaylist(playlist: Playlist, trackId: Long) {
        playlistRepository.deleteTrackInPlaylist(playlist, trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }
}