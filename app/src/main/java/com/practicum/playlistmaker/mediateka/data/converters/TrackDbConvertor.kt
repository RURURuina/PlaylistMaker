package com.practicum.playlistmaker.mediateka.data.converters

import com.practicum.playlistmaker.mediateka.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

object TrackDbConvertor {
    fun Track.toFavoriteTrackEntity(): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    fun FavoriteTrackEntity.toTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            trackTimeMillis,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }
}