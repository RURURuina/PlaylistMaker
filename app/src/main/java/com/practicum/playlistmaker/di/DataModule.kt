package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.network.ITunesService
import com.practicum.playlistmaker.search.data.preferences.SearchHistory
import com.practicum.playlistmaker.search.data.preferences.SearchHistoryStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val baseUrl = "https://itunes.apple.com"
private const val SEARCH_HISTORY = "search_history"
private const val THEME = "playlistmaker_theme"

val dataModule = module {
    single<ITunesService> {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesService::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    }

    single {
        Gson()
    }

    single<SearchHistory> {
        SearchHistoryStorage(sharedPreferences = get(), historyGson = get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single(named("Theme_prefs")) {
        androidContext().getSharedPreferences(THEME, Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}