package com.practicum.playlistmaker.mediateka.data.converters

import com.practicum.playlistmaker.mediateka.data.db.TrackInPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track

object TrackInPlaylistDbConvertor {

    fun Track.toTrackInPlaylistEntity(): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
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

    fun TrackInPlaylistEntity.toTrack(): Track {
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