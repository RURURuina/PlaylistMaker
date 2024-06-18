package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {
    private val _tracks = MutableLiveData<MutableList<Track?>>()
    val tracks: LiveData<MutableList<Track?>> get() = _tracks

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun searchTracks(term: String) {
        interactor.searchTracks(term) { result ->
            result.fold(
                onSuccess = { tracks ->
                    _tracks.postValue(tracks)
                },
                onFailure = { error ->
                    _error.postValue(error.message)
                }
            )
        }
    }

    fun getSearchHistory(): MutableList<Track?> {
        return interactor.getSearchHistory()
    }

    fun addTrackToHistory(track: Track) {
        interactor.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
    }
}