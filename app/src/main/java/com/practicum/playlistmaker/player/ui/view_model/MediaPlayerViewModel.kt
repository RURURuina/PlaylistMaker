package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.ui.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(private val interactor: MediaPlayerInteractor) : ViewModel() {
    private companion object {
        const val TRACK_FINISH = 29_900L

    }

    private val handler = Handler(Looper.getMainLooper())
    private val _playerState = MutableLiveData<PlayerState>().apply { value = PlayerState.DEFAULT }
    val playerState: LiveData<PlayerState> = _playerState

    private fun startTimer() {
        handler?.postDelayed(
            object : Runnable {
                override fun run() {
                    val maxTrackDuration: Long =
                        if (interactor.playerDuration > TRACK_FINISH) {
                            TRACK_FINISH
                        } else {
                            (interactor.playerDuration.toLong())
                        }
                    if (interactor.playerCurrentPosition < maxTrackDuration) {
                        renderState(
                            PlayerState.CurrentPosition(
                                SimpleDateFormat(
                                    "mm:ss",
                                    Locale.getDefault()
                                ).format(interactor.playerCurrentPosition)
                            )
                        )
                    } else {
                        renderState(PlayerState.PREPARED)
                    }
                    handler?.postDelayed(
                        this, 500
                    )
                }
            }, 500
        )
    }

    fun renderState(stateUi: PlayerState) {
        _playerState.postValue(stateUi)
    }

    fun preparePlayer(previewUrl: String?) {
        interactor.preparePlayer(previewUrl, {
            _playerState.value = PlayerState.PREPARED
        }, {
            _playerState.value = PlayerState.PREPARED
        })
    }

    private fun startPlayer() {
        interactor.startPlayer()
        _playerState.value = PlayerState.PLAYING
        startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        _playerState.value = PlayerState.PAUSED
        handler.removeCallbacksAndMessages(null)
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