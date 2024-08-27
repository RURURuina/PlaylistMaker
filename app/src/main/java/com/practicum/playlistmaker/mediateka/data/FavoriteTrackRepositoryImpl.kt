package com.practicum.playlistmaker.mediateka.data

import com.practicum.playlistmaker.mediateka.data.converters.TrackDbConvertor.toFavoriteTrackEntity
import com.practicum.playlistmaker.mediateka.data.converters.TrackDbConvertor.toTrack
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.mediateka.domain.db.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTrackRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.favoriteTrackDao().addTrackToFavorites(track.toFavoriteTrackEntity())
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        appDatabase.favoriteTrackDao().removeTrackFromFavorites(track.toFavoriteTrackEntity())
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getAllFavoriteTracks()
            .map { it.map {entity -> entity.toTrack()} }
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        val favoriteTrackIds = appDatabase.favoriteTrackDao().getFavoriteTrackIds()
        return favoriteTrackIds.contains(trackId)
    }
}