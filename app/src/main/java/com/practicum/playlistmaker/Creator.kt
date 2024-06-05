package com.practicum.playlistmaker

import android.media.MediaPlayer
import com.practicum.playlistmaker.data.repositories.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.api.MediaPlayerRepository
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl

object Creator {
    fun mediaPlayerCreator(): MediaPlayerInteractor {

        val mediaPlayer = MediaPlayer()
        val repository: MediaPlayerRepository = MediaPlayerRepositoryImpl(mediaPlayer)
        return MediaPlayerInteractorImpl(repository)
    }
}