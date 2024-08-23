package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track?>>()
    val tracks: LiveData<List<Track?>> get() = _tracks

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _searchHistory = MutableLiveData<List<Track?>>()
    val searchHistory: LiveData<List<Track?>> get() = _searchHistory


    fun searchTracks(term: String) {
        viewModelScope.launch {
            interactor.searchTracks(term).catch { e -> _error.value = e.message }
                .collect { result ->
                    result.fold(onSuccess = { _tracks.value = it },
                        onFailure = { _error.value = it.message })
                }
        }
    }

    fun loadSearchHistory() {
        _searchHistory.postValue(interactor.getSearchHistory())
    }

    fun addTrackToHistory(track: Track) {
        interactor.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
    }
}