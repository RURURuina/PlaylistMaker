package com.practicum.playlistmaker.domain.api

interface MediaPlayerInteractor {
    fun preparePlayer(previewUrl: String?, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}