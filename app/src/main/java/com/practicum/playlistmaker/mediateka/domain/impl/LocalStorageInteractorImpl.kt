package com.practicum.playlistmaker.mediateka.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.mediateka.domain.LocalStorageInteractor
import com.practicum.playlistmaker.mediateka.domain.LocalStorageRepository

class LocalStorageInteractorImpl(private val localStorageRepository: LocalStorageRepository) :
    LocalStorageInteractor {

    override fun saveImageToLocalStorage(uri: Uri): String {
        return localStorageRepository.saveImageToLocalStorage(uri)
    }
}