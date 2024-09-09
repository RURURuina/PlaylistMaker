package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.ui.model.PlayerState
import com.practicum.playlistmaker.player.ui.model.TrackInPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(
    private val interactor: MediaPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor) : ViewModel() {


    private val _playerState = MutableLiveData<PlayerState>().apply { value = PlayerState.DEFAULT }
    val playerState: LiveData<PlayerState> = _playerState

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavoritesState(): LiveData<Boolean> = favoriteLiveData

    private val playlistLiveData = MutableLiveData<List<Playlist>>()
    fun observePlaylist(): LiveData<List<Playlist>> = playlistLiveData

    private val trackInPlaylistLiveData = MutableLiveData<TrackInPlaylistState>()
    fun observeTrackInPlaylistState(): LiveData<TrackInPlaylistState> = trackInPlaylistLiveData


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

    fun preparePlayer(previewUrl: String?, track: Track) {
        viewModelScope.launch(Dispatchers.IO) {

            val isFavorite = favoriteTrackInteractor.isTrackFavorite(track.trackId)
            favoriteLiveData.postValue(isFavorite)
        }

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
    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if (favoriteTrackInteractor.isTrackFavorite(track.trackId)) {
                favoriteTrackInteractor.removeTrackFromFavorites(track)
                favoriteLiveData.postValue(false)
            } else {
                favoriteTrackInteractor.addTrackToFavorites(track)
                favoriteLiveData.postValue(true)
            }
        }
    }
    fun getSavedPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getSavedPlaylists().collect() {playlists ->
                playlistLiveData.postValue(playlists)
            }
        }
    }
    fun addTracksIdInPlaylist(playlist: Playlist, tracksId: List<Long>, track: Track) {
        if (track.trackId in tracksId) {
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackIsAlreadyInPlaylist(playlist.playlistName))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor.addTrackIdInPlaylist(playlist, tracksId, track)
            }
            trackInPlaylistLiveData.postValue(TrackInPlaylistState.TrackAddToPlaylist(playlist.playlistName))
        }
    }
}