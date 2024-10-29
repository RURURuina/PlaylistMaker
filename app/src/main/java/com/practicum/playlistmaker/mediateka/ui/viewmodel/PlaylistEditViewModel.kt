package com.practicum.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.LocalStorageInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistEditViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistInteractor: PlaylistInteractor,
    val playlist: Playlist
) : NewPlaylistViewModel(localStorageInteractor, playlistInteractor) {

    var playlistDb: Playlist? = null


    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observeStatePlaylist(): LiveData<Playlist> = playlistLiveData


    fun getPlaylistById() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylistById(playlist.playlistId).collect { playlistById ->
                if (playlistById != null) {
                    playlistDb = playlistById
                    playlistLiveData.postValue(playlistDb!!)
                }
            }
        }
    }

    fun editPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(
                Playlist(
                    playlistId = playlistDb!!.playlistId,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = if (uri.isNullOrEmpty()) {
                        playlistDb!!.uri
                    } else {
                        uri
                    },
                    tracksIdInPlaylist = playlistDb!!.tracksIdInPlaylist,
                    tracksCount = playlistDb!!.tracksCount
                )

            )
        }

    }
}