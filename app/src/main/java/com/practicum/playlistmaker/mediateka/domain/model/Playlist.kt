package com.practicum.playlistmaker.mediateka.domain.model

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val uri: String?,
    val tracksIdInPlaylist: List<Long>,
    val tracksCount: Int
)
