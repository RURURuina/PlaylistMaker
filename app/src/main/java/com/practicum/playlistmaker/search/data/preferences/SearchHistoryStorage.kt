package com.practicum.playlistmaker.search.data.preferences

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryStorage(
    private val sharedPreferences: SharedPreferences,
    private val historyGson: Gson
) : SearchHistory {

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        const val MAX_HISTORY_SIZE = 10
    }


    override fun getSearchHistory(): MutableList<Track?> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (!historyJson.isNullOrEmpty()) {
            historyGson.fromJson(historyJson, object : TypeToken<MutableList<Track>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    override fun clearSearchHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    override fun saveSearchHistory(history: MutableList<Track?>) {
        val historyJson = historyGson.toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, historyJson).apply()
    }

    fun isNotEmpty(): Boolean {
        return getSearchHistory().isNotEmpty()
    }
}