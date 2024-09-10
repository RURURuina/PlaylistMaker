package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchFragmentState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {

    private val _screenState = MutableLiveData<SearchFragmentState>()
    val screenState: LiveData<SearchFragmentState> get() = _screenState

    private val _searchResult = MutableLiveData<MutableList<Track?>>()
    val searchResult = _searchResult



    var currentQuery: String = ""

    fun searchTracks(term: String) {

        if (term == currentQuery && !_searchResult.value.isNullOrEmpty()) {
            _screenState.value = SearchFragmentState.Content(_searchResult.value!!)
            return
        }

        currentQuery = term
        _screenState.value = SearchFragmentState.Loading

        viewModelScope.launch {
            interactor.searchTracks(term)
                .catch { e ->
                    _screenState.value = SearchFragmentState.Error(e.message ?: "Unknown error")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _searchResult.value = it.toMutableList()
                            if (it.isEmpty()) {
                                _screenState.value = SearchFragmentState.Empty
                            } else {
                                _screenState.value = SearchFragmentState.Content(it.toMutableList())
                            }
                        },
                        onFailure = {
                            _screenState.value = SearchFragmentState.Error(it.message ?: "Unknown error")
                        }
                    )
                }
        }
    }

    fun loadSearchHistory() {
        val history = interactor.getSearchHistory()
        if (history.isEmpty()) {
            _screenState.value = SearchFragmentState.Empty
        } else {
            _screenState.value = SearchFragmentState.History(history.toMutableList())
        }
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        _screenState.value = SearchFragmentState.Empty
    }

    fun addTrackToHistory(track: Track) {
        interactor.addTrackToHistory(track)
        loadSearchHistory()
    }

}
