package com.practicum.playlistmaker.domain.api

interface MediaPlayerRepository {
    fun preparePlayer(previewUrl: String?, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}