package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val repository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    override fun preparePlayer(
        previewUrl: String?, onPrepared: () -> Unit, onCompletion: () -> Unit
    ) {
        repository.preparePlayer(previewUrl, onPrepared, onCompletion)
    }

    override val playerCurrentPosition: Int
        get() = repository.playerCurrentPosition

    override val playerDuration: Int
        get() = repository.playerDuration

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }


    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}