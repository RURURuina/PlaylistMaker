package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.SearchResponse
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.preferences.SearchHistory
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import retrofit2.Call
import retrofit2.Response

class TrackRepositoryImpl(
    private val iTunesService: ITunesService,
    private val searchHistory: SearchHistory,

    ) : TrackRepository {

    override fun searchTracks(term: String, callback: (Result<MutableList<Track?>>) -> Unit) {
        val call = iTunesService.search(term)
        call.enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    searchResponse?.let {
                        callback(Result.success(it.results))
                    } ?: callback(Result.failure(Exception("Empty response body")))
                } else {
                    callback(Result.failure(Exception("${response.code()}")))
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun getSearchHistory(): MutableList<Track?> {
        return searchHistory.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        val history = searchHistory.getSearchHistory()
        history.removeIf { it?.trackId == track.trackId }
        history.add(0, track)
        if (history.size > SearchHistory.MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        searchHistory.saveSearchHistory(history)
    }

    override fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }

}