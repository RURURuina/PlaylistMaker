package com.practicum.playlistmaker.mediateka.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.LocalStorageInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistName = ""
    private var playlistDescription: String? = null
    private var uri: String? = null
    private var tracksCount = 0

    fun saveImageToLocalStorage(uri: Uri) {
        localStorageInteractor.saveImageToLocalStorage(uri)
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.createPlaylist(
                Playlist(
                    playlistId = 0,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = uri,
                    tracksIdInPlaylist = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }

    fun setPlaylistName(playlistName: String) {
        this.playlistName = playlistName
    }

    fun setPlaylistDescription(playlistDescription: String) {
        this.playlistDescription = playlistDescription
    }

    fun setUri(uri: Uri?) {
        this.uri = uri?.toString()
    }
}