package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.ui.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(private val interactor: MediaPlayerInteractor) : ViewModel() {


    private val _playerState = MutableLiveData<PlayerState>().apply { value = PlayerState.DEFAULT }
    val playerState: LiveData<PlayerState> = _playerState


    private var timerUpdateJob: Job? = null

    private companion object {
        const val UPDATE_INTERVAL = 300L
    }

    private fun startTimer() {


        timerUpdateJob = viewModelScope.launch {
            while (interactor.isPlaying()) {
                delay(UPDATE_INTERVAL)
                _playerState.postValue(
                    PlayerState.CurrentPosition(
                        SimpleDateFormat(
                            "mm:ss", Locale.getDefault()
                        ).format(interactor.playerCurrentPosition) ?: "00:00"
                    )
                )
            }
        }
    }

    fun startTime(): String {
        return String.format("%02d:%02d", 0, 0)
    }

    fun preparePlayer(previewUrl: String?) {
        interactor.preparePlayer(previewUrl, {
            _playerState.value = PlayerState.PREPARED
        }, {
            _playerState.value = PlayerState.PREPARED
            timerUpdateJob?.cancel()


        })
    }


    fun startPlayer() {
        if (_playerState.value == PlayerState.PREPARED || _playerState.value == PlayerState.PAUSED) {
            interactor.startPlayer()
            _playerState.value = PlayerState.PLAYING
            startTimer()

        }
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playerState.value = PlayerState.PAUSED
        timerUpdateJob?.cancel()

    }

    fun releasePlayer() {
        interactor.releasePlayer()

    }


    fun isPlaying(): Boolean {
        return interactor.isPlaying()
    }


    fun playbackControl() {
        if (isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }
}