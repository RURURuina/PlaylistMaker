package com.practicum.playlistmaker.mediateka.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.LocalStorageInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    open var playlistName = ""
    open var playlistDescription: String? = ""
    open var uri: String? = ""
    private var tracksCount = 0

    fun saveImageToLocalStorage(uri: Uri): String? {
        return localStorageInteractor.saveImageToLocalStorage(uri)
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
}