package com.practicum.playlistmaker.player.domain.api

interface MediaPlayerInteractor {

    val playerCurrentPosition: Int
    val playerDuration: Int
    fun preparePlayer(previewUrl: String?, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}