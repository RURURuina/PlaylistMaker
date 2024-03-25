package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun addTrackToHistory (track: Track) {
        val history = getSearchHistory()

        history.removeIf {it.trackId == track.trackId}

        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveSearchHistory(history)
    }

     fun getSearchHistory() : MutableList<Track> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (!historyJson.isNullOrEmpty()) {
            Gson().fromJson(historyJson, object : TypeToken<MutableList<Track>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveSearchHistory (history: MutableList<Track>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, historyJson).apply()
    }

    fun isNotEmpty(): Boolean {
        return getSearchHistory().isNotEmpty()
    }
}