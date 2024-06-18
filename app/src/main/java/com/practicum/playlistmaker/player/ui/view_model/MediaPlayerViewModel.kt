package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState

class MediaPlayerViewModel(private val interactor: MediaPlayerInteractor) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>().apply { value = PlayerState.DEFAULT }
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    fun preparePlayer(previewUrl: String?) {
        interactor.preparePlayer(previewUrl, {
            _playerState.value = PlayerState.PREPARED
        }, {
            _playerState.value = PlayerState.PREPARED
        })
    }

    fun startPlayer() {
        interactor.startPlayer()
        _playerState.value = PlayerState.PLAYING
        updateCurrentPosition()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playerState.value = PlayerState.PAUSED
    }

    fun releasePlayer() {
        interactor.releasePlayer()
    }

    fun updateCurrentPosition() {
        _currentPosition.value = interactor.getCurrentPosition()
    }

    fun isPlaying(): Boolean {
        return interactor.isPlaying()
    }
}