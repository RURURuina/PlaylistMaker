package com.practicum.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.network.RetrofitClient
import com.practicum.playlistmaker.search.data.preferences.SearchHistory
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.impl.TrackInteractorImpl

class SearchViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val iTunesService: ITunesService =
                RetrofitClient.getClient(application.getString(com.practicum.playlistmaker.R.string.itunes_url))
                    .create(ITunesService::class.java)

            val sharedPreferences = Creator.provideSharedPreferences(application)
            val searchHistory = SearchHistory(sharedPreferences)
            val repository: TrackRepository = TrackRepositoryImpl(iTunesService, searchHistory)
            val interactor: TrackInteractor = TrackInteractorImpl(repository)
            return SearchViewModel(interactor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}