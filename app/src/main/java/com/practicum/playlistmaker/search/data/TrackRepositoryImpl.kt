package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.preferences.SearchHistory
import com.practicum.playlistmaker.search.data.preferences.SearchHistoryStorage
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val iTunesService: ITunesService,
    private val searchHistory: SearchHistory,
    private val appDatabase: AppDatabase
    ) : TrackRepository {

    override suspend fun searchTracks(term: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = iTunesService.search(term)
            if (response.isSuccessful) {
                val results: List<Track> = response.body()?.results?.filterNotNull() ?: emptyList()
                val favoriteTrackIds = appDatabase.favoriteTrackDao().getFavoriteTrackIds()
                val trackWithFavoriteStatus = results.map {track: Track ->
                    track.copy(isFavorite = favoriteTrackIds.contains(track.trackId))
                }

                emit(Result.success(trackWithFavoriteStatus))
            } else {
                emit(Result.failure(Exception("Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getSearchHistory(): MutableList<Track?> {
        return searchHistory.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        val history = searchHistory.getSearchHistory()
        history.removeIf { it?.trackId == track.trackId }
        history.add(0, track)
        if (history.size > SearchHistoryStorage.MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        searchHistory.saveSearchHistory(history)
    }

    override fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }

}