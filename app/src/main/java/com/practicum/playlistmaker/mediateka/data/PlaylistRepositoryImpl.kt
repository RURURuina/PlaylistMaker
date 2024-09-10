package com.practicum.playlistmaker.mediateka.data

import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConvertor.toPlaylist
import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConvertor.toPlaylistEntity
import com.practicum.playlistmaker.mediateka.data.converters.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase): PlaylistRepository {


    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: ArrayList<Long>,
        track: Track
    ) {
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        tracksId.add(track.trackId)
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = tracksId,
            tracksCount = playlist.tracksCount + 1
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
    }



    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getSavedPlaylist().map {
            it.map {entity -> entity.toPlaylist()}
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlist.toPlaylistEntity())
    }
}