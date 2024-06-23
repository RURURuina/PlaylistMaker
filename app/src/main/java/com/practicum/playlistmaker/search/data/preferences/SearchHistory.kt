package com.practicum.playlistmaker.search.data.preferences

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistory {
    fun getSearchHistory(): MutableList<Track?>
    fun clearSearchHistory()
    fun saveSearchHistory(history: MutableList<Track?>)
}